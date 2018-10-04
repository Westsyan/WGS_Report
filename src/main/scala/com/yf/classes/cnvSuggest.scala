package com.yf.classes

import java.io.{File, FileOutputStream}
import java.util

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.text.pdf.{BaseFont, PdfStamper}
import com.itextpdf.text.{BaseColor, Rectangle}
import com.yf.classes.illSuggest.addCnvToPDF
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

import scala.util.control.Breaks._

object cnvSuggest {

  def dealWithCnv(dataPath: String, resPath: String, counts: Int, sex: String, tmpPath: String) = {
    val medicine_x = Array(143.75f, 244f, 344.25f, 444.5f)
    //    val drugEffect = new File(dataPath).listFiles().filter(_.getName.contains("DrugEffect")).head
    val drugEffect = new File(dataPath + ".DrugEffect.txt")
    val medicineFile = FileUtils.readLines(drugEffect, "UTF-8").asScala
    val medic = medicineFile.map { x =>
      val me = x.split("\t")
      (me.head.split("\uFEFF").last, me.last)
    }
    val medicine = medic.filter(_._2 == "疗效较弱").map(_._1)
    var medicine_y = 612.55f
    var count = 0
    val black = new DeviceRgb(62, 62, 62)
    val white = new DeviceRgb(255, 255, 255)
    var position = Array[(String, Float, Float, DeviceRgb)]()
    var medicine_background = 624.81f
    for (m <- medicine) {
      position = position :+ ("● " + m, medicine_x(count), medicine_y, black)
      count += 1
      if (count == 4) {
        count = 0
        medicine_y = medicine_y - 22.55f
      } else if (count == 1) {
        medicine_background = medicine_background - 22.5f
      }
    }
    //1：143.75，2：244，3：344.25，4：444.5 行间距：22.55
    //第一行：602.31，间隔：22.5
    //药物与CNV间隔：34.23f,CNV标题长度28.36f,文字和标题间隔22.78f
    var coord = Array((127.56f, 639.21f, 558.42f, medicine_background, new BaseColor(236, 236, 237)))
    /*
    CNV文字插入
     */
    // val reportfile = new File(dataPath).listFiles().filter(_.getName.contains("CNVreport")).head
    val reportfile = new File(dataPath + ".CNVreport.txt")
    val cnvreport = FileUtils.readLines(reportfile, "UTF-8").asScala
    var page = counts + 1
    var side = 1
    val rexp="^\\s*$".r
    val cnvreports = FileUtils.readFileToString(reportfile, "UTF-8")
//    rexp.findFirstIn()
    val isCnv = cnvreport.headOption.getOrElse("").split("\t").length
    if (!cnvreports.matches("^\\s*$")) {
      coord = coord :+ (127.56f, medicine_background - 34.23f, 558.42f, medicine_background - 62.59f, new BaseColor(41, 124, 192))
      val getCnv = cnvreport.map(_.split("\t").last)
      val cx = getCnv.mkString(";").split(";").map(_.split(","))
      val cnvBefore = cx.map { x =>
        val w = cx.filter(y => y(1) == x(1)).sortBy(_.last).map(_.mkString(",")).last
        w
      }.distinct
      val cnvSize = getCnv.map(_.split(";").size / 2)
      val pagi = sex match {
        case "men" => 326
        case "women" => 336
      }
      val suggestFileSize = new File(tmpPath + "/suggestTmp").listFiles().size
      val cnv = cnvBefore.map { x =>
        val tr = x.split(",")
        val findCnvIndex = getCnv.map { y =>
          val i1 = getCnv.indexOf(y)
          val i2 = y.split(";").indexOf(x)
          (i1, i2)
        }.filter(_._1 != -1).filter(_._2 != -1).head
        var cnvCount = 0
        for (i <- cnvSize.take(findCnvIndex._1)) {
          cnvCount = cnvCount + i
        }
        val cnvpage = cnvCount + findCnvIndex._2 / 2 + pagi - 5 + suggestFileSize
        (tr(1), cnvpage.toString, tr.last + "风险")
      }.distinct
      position = position :+ (s"根据您的全基因组测序结果显示，您有 ${cnv.length} 个致病性拷贝数变异（CNV）, 可能导致以下疾病发生：", 150f, medicine_background - 57.01f, white)

      //   val cnv_background_y = if(medicine_background-74.59f-25.37f*cnv.length < 46f){46f}else{medicine_background-74.59f-25.37f*cnv.length}
      //  coord = coord :+  (127.56f, medicine_background - 62.59f, 558.42f,cnv_background_y, new BaseColor(236, 236, 237))
      var cnv_y = medicine_background - 62.59f - 6f
      var cnv_risk_y = cnv_y - 20.39f + 25.37f
      var cnv_line_y = cnv_y - 30.37f
      var cnv_background_y = if (medicine_background - 74.59f - 25.37f * cnv.length < 46f) {
        46f
      } else {
        medicine_background - 74.59f - 25.37f * cnv.length
      }
      coord = coord :+ (127.56f, medicine_background - 62.59f, 558.42f, cnv_background_y, new BaseColor(236, 236, 237))
      var cnv_count = 0
      var cnv_nums = 77


      for (c <- cnv) {
        cnv_count += 1
        cnv_y = cnv_y - 25.37f
        cnv_risk_y = cnv_risk_y - 25.37f

        val all_x = getX(side, c)
        val (name_x, risk_x, page_x, cnv_risk_x) = (all_x._1, all_x._2, all_x._3, all_x._4)
        val cnv_color = c._3 match {
          case "低风险" => new BaseColor(109, 190, 89)
          case "中风险" => new BaseColor(244, 129, 35)
          case "较高风险" => new BaseColor(134, 82, 159)
          case "高风险" => new BaseColor(237, 40, 102)
        }

        if (cnv_y > 46f) {
          position = position :+ ("●  " + c._1, name_x, cnv_y, black) //疾病名
          position = position :+ (c._2 + "页", page_x, cnv_y, black)
          //页码
          position = position :+ (c._3, cnv_risk_x, cnv_y, white)
          //添加风险等级背景色
          coord = coord :+ (cnv_risk_y, risk_x, 0f, 1f, cnv_color)
          //添加线条
          if (cnv_line_y > 50f) {
            coord = coord :+ (cnv_line_y, 0f, 0f, 2f, new BaseColor(62, 62, 63))
            cnv_line_y = cnv_line_y - 25.37f
          }
        } else {
          side = addPageToPdf(position, coord, side, page, resPath, sex, tmpPath)
          cnv_y = 701.72f - 25.37f
          val first_x = getX(side, c)
          position = Array(("●  " + c._1, first_x._1, cnv_y, black)) //疾病名
          position = position :+ (c._2 + "页", first_x._3, cnv_y, black)
          position = position :+ (c._3, first_x._4, cnv_y, white)
          cnv_risk_y = 701.72f - 20.39f
          cnv_line_y = 701.72f - 30.37f
          cnv_nums = cnv_nums - cnv_count + 1
          cnv_count = 1
          cnv_background_y = if (689.72f - 25.37f * cnv_nums < 46f) {
            46f
          } else {
            689.72f - 25.37f * cnv_nums
          }
          coord = Array((getLeft(side), 701.72f, getLeft(side) + 430.86f, cnv_background_y, new BaseColor(236, 236, 237)))

          page += 1
          coord = coord :+ (cnv_risk_y, first_x._2, 0f, 1f, cnv_color)
        }
      }
      var (cnv_title_x, cnv_text_x) = getTAT(side)
      //34.23:CNV底部与顶框的距离，48.7：框的高度
      coord = coord :+ (cnv_background_y - 82.93f, cnv_title_x, 0f, 3f, new BaseColor(14, 123, 191))
      position = position :+ ("\t针对上述疾病，建议您注意了解疾病临床表型特征及诱发因素，积极采取以下健康建议，争取做\n到早预防、早诊断、早干预、早治疗。", cnv_text_x, cnv_background_y - 74.13f, new DeviceRgb(14, 123, 191))
      side = addPageToPdf(position, coord, side, page, resPath, sex, tmpPath)
      side = getSaveSide(side)
      val last = addCnvToPDF(resPath, cnv.map(_._1), cnv_background_y - 105.73f, page, side, sex, tmpPath,dataPath)
      //生成体质建议
      corporSuggest(last._1, resPath, dataPath, last._2, sex, tmpPath)
    } else {
      addPageToPdf(position, coord, side, page, resPath, sex, tmpPath)
      corporSuggest(2, resPath, dataPath, page + 1, sex, tmpPath)
    }
  }


  def corporSuggest(side: Int, resPath: String, dataPath: String, count: Int, sex: String, tmpPath: String) = {
    val left = getLeft(side)
    var coord = Array((left, 701.72f, left + 430.86f, 654.4f, new BaseColor(41, 124, 192)),
      (654.4f - 22.7f, 0f, 0f, 5f, new BaseColor(62, 62, 63)))
    var head = 626.7f
    //  val physical = new File(dataPath).listFiles().filter(_.getName.contains("PhysicalCharacter")).head
    val physical = new File(dataPath + ".PhysicalCharacter.txt")
    val cin = FileUtils.readLines(physical, "UTF-8").asScala
    val ctext = cin.map { x =>
      val r = x.split("\t")
      val corporHead = r.head.split("\uFEFF").last
      val index = r.last match {
        case "OR大" => 1
        case "OR中" => 2
        case "OR小" => 3
      }
      val text = try {
        val file = suggest.bodynumsMap.get(corporHead).get
        val suggestPath = resPath + "/suggest/corporeity/" + file + "/" + index + ".txt"
        FileUtils.readFileToString(new File(suggestPath), "UTF-8")
      } catch {
        case e: Exception =>
          println("报告中不存在此体质信息： " + corporHead + "\n请删除后再次运行，程序终止！")
          break()
      }
      (index, text)
    }
    val black = new DeviceRgb(62, 62, 62)
    val white = new DeviceRgb(255, 255, 255)
    var position = Array[(String, Float, Float, DeviceRgb)]()
    val cnv_text_x = getTAT(side)._2
    position = Array(("除了肿瘤、疾病和用药以外，在基因层面我们还从常见体质特征、运动健身、营养需求、皮肤管\n理四个方面对您的体质、代谢等状况进行基因层面的解析，以下是我们给出的建议：",
      cnv_text_x, 660f, white))
    ctext.filter(_._1 == 1).map { x =>

      val line = x._2.size / 46
      val column = if (line < 1) {
        21.15f
      } else {
        36f
      }
      head = head - column
      position = position :+ ("● " + x._2, left, head + 2f, black)
      coord = coord :+ (head, 0f, 0f, 6f, new BaseColor(62, 62, 63))
    }
    coord = coord :+ (head, 0f, 0f, 5f, new BaseColor(62, 62, 63))
    val (model, formPage) = if (side == 3) {
      (resPath + "/model/" + sex + "/" + sex + " 13.pdf", "12")
    } else {
      (resPath + "/model/" + sex + "/" + sex + " 14.pdf", "13")
    }
    val map = new util.HashMap[String, Object]()
    val o = new util.HashMap[String, Object]()
    map.put(formPage, "0" + (count + 8).toString)
    o.put("datemap", map)
    val file1 = tmpPath + "/suggestTmp/" + count + ".pdf"
    val file2 = tmpPath + "/suggestTmp1/" + count + ".pdf"
    PdfUtils.pdfoutWithText(o, resPath + "/fonts/msyh.ttc,1", model, file1, resPath)
    addElementToPdf(file1, file2, coord, side)
    addContentToPdf(file2, file1, position, side, resPath)
    if (side == 3) {
      FileUtils.copyFile(new File(resPath + "/model/nullPage.pdf"), new File(tmpPath + "/suggestTmp/" + (count + 1) + ".pdf"))
    }
  }


  def getTAT(side: Int): (Float, Float) = {
    if (side == 1 || side == 3) {
      (127.55f, 143.77f)
    } else {
      (649.07f, 665.29f)
    }
  }

  def getSaveSide(side: Int): Int = side match {
    case 3 => 2
    case 2 => 3
  }

  def addPageToPdf(position: Array[(String, Float, Float, DeviceRgb)], coord: Array[(Float, Float, Float, Float, BaseColor)], side: Int, count: Int, resPath: String, sex: String, tmpPath: String): Int = {
    var s = 0
    val map = new util.HashMap[String, Object]()
    val o = new util.HashMap[String, Object]()
    val file1 = tmpPath + "/suggestTmp/" + count + ".pdf"
    val file2 = tmpPath + "/suggestTmp1/" + count + ".pdf"
    if (side == 1) {
      map.put("15", "0" + (count + 8).toString)
      o.put("datemap", map)
      PdfUtils.pdfoutWithText(o, resPath + "/fonts/msyh.ttc,1", resPath + "/model/" + sex + "/" + sex + " 15.pdf", file1, resPath)
      addElementToPdf(file1, file2, coord, side)
      addContentToPdf(file2, file1, position, side, resPath)
      s = 2
    } else if (side == 2) {
      map.put("13", "0" + (count + 8).toString)
      o.put("datemap", map)
      PdfUtils.pdfoutWithText(o, resPath + "/fonts/msyh.ttc,1", resPath + "/model/" + sex + "/" + sex + " 14.pdf", file1, resPath)
      addElementToPdf(file1, file2, coord, side)
      addContentToPdf(file2, file1, position, side, resPath)
      s = 3
    } else {
      map.put("12", "0" + (count + 8).toString)
      o.put("datemap", map)
      PdfUtils.pdfoutWithText(o, resPath + "/fonts/msyh.ttc,1", resPath + "/model/" + sex + "/" + sex + " 13.pdf", file1, resPath)
      addElementToPdf(file1, file2, coord, side)
      addContentToPdf(file2, file1, position, side, resPath)
      s = 2
    }
    s
  }

  def getLeft(side: Int): Float = side match {
    case 2 => 649.07f
    case _ => 127.56f
  }

  def getX(side: Int, c: (String, String, String)): (Float, Float, Float, Float) = {
    val (name_x, risk_x, page_x) = if (side == 1 || side == 3) {
      (143.76f, 257f, 334.1f)
    } else {
      (665.27f, 778.51f, 839.41f)
    }
    val cnv_risk_x =
      if (side == 1 || side == 3) {
        if (c._3.size == 4) {
          257.6f
        } else {
          262f
        } //较高风险
      } else {
        if (c._3.length == 4) {
          779.11f
        } else {
          783.51f
        } //较高风险
      }
    (name_x, risk_x, page_x, cnv_risk_x)
  }

  def addContentToPdf(inpath: String, outpath: String, position: Array[(String, Float, Float, DeviceRgb)], side: Int, resPath: String): Unit = {
    val reader = new com.itextpdf.kernel.pdf.PdfReader(inpath)
    val writer = new PdfWriter(outpath)
    val pdfDoc = new PdfDocument(reader, writer)
    val document = new Document(pdfDoc)
    val font = PdfFontFactory.createFont(resPath + "/fonts/msyh.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
    val width = 430
    val color = new DeviceRgb(62, 62, 62)
    //setFixedLeading :设置行高
    for (p <- position) {
      val title = new Paragraph(p._1).setFont(font).setFontSize(9).setFontColor(p._4).setTextAlignment(TextAlignment.LEFT).setFixedLeading(16.24f).setFixedPosition(1, p._2, p._3, width)
      if (p._2 == 143.77f || p._2 == 665.29f) {
        title.setFirstLineIndent(18.65f)
      }
      document.add(title)
    }
    document.close()
    reader.close()
    writer.close()
    pdfDoc.close()
  }

  def addElementToPdf(inpath: String, outpath: String, coord: Array[(Float, Float, Float, Float, BaseColor)], side: Int): Unit = {
    val reader = new com.itextpdf.text.pdf.PdfReader(inpath)
    val stamper = new PdfStamper(reader, new FileOutputStream(outpath))
    val over = stamper.getOverContent(1)
    over.setColorStroke(new BaseColor(155, 132, 121))
    for (c <- coord) {
      if (c._4 == 1f) {
        over.setLineWidth(6f)
        //虚线不设置
        over.setLineDash(1f)
        over.setColorStroke(c._5)
        over.roundRectangle(c._2, c._1, 37f, 6f, 0.05f)
        over.stroke()
      } else if (c._4 == 2f) {
        val (x1, x2) = if (side == 1 || side == 3) {
          (144f, 542f)
        } else {
          (665.51f, 1063.51f)
        }
        //线的颜色
        over.setColorStroke(c._5)
        //虚线
        over.setLineDash(2f, 0.1f)
        //线的粗度
        over.setLineWidth(0.3f)
        //起始坐标
        over.moveTo(x1, c._1)
        //终止坐标
        over.lineTo(x2, c._1)
        over.stroke()
      } else if (c._4 == 3f) {
        over.setColorStroke(c._5)
        over.setLineDash(1f)
        over.setLineWidth(0.3f)
        over.rectangle(c._2, c._1, 430.86f, 48.7f)
        over.stroke()
      } else if (c._4 == 5f) {
        val (x1, x2) = if (side == 1 || side == 3) {
          (127.56f, 558.42f)
        } else {
          (649.07f, 1079.33f)
        }
        over.setColorStroke(c._5)
        over.setLineDash(1f)
        over.setLineWidth(0.3f)
        over.moveTo(x1, c._1)
        over.lineTo(x2, c._1)
        over.stroke()
      } else if (c._4 == 6f) {
        val (x1, x2) = if (side == 1 || side == 3) {
          (127.56f, 558.42f)
        } else {
          (649.07f, 1079.33f)
        }
        over.setColorStroke(c._5)
        over.setLineDash(2f, 0.1f)
        over.setLineWidth(0.3f)
        over.moveTo(x1, c._1)
        over.lineTo(x2, c._1)
        over.stroke()
      }
      else {
        //1，2起始坐标；3，4终止坐标
        val r = new Rectangle(c._1, c._2, c._3, c._4)
        r.setBackgroundColor(c._5)

        over.rectangle(r)
        over.stroke()
      }
    }
    stamper.close()
    reader.close()
  }

  def addTextToPdf(path: String, name: Array[(String, String)], sex: String, tmpPath: String): Unit = {
    val array = suggest.bodyMap

      for (n <- name) {
        if (n._1 != "蛋白质代谢水平" && n._1 != "碳水化合物代谢水平" && n._1 != "脂质代谢水平") {

        val a = array.filter(_._1 == n._1).head
        val page = if (sex == "men") {
          a._3
        } else {
          a._3 + 10
        }
        val inpath = tmpPath + "/mainTmp/" + page + ".pdf"
        val outpath = tmpPath + "/mainTmp2/" + page + ".pdf"
        val suggestMap = suggest.bodynumsMap
        val file = suggestMap.get(a._1).get
        val reader = new com.itextpdf.kernel.pdf.PdfReader(inpath)
        val writer = new PdfWriter(outpath)
        val pdfDoc = new PdfDocument(reader, writer)
        val document = new Document(pdfDoc)
        val font = PdfFontFactory.createFont(path + "/fonts/msyhbd.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        val color = new DeviceRgb(255, 241, 0)
        //setFixedLeading :设置行高
        val left = if (a._4 == 1) {
          60f
        } else if (a._4 == 3) {
          53f
        }
        else {
          665f
        }
        var bottom = a._2
        val num = n._2 match {
          case "OR大" => 1
          case "OR中" => 2
          case "OR小" => 3
        }
        val text = FileUtils.readFileToString(new File(path + "/suggest/corporeity/" + file + "/" + num + ".txt"), "UTF-8")
        bottom = bottom - 16.24f * (text.size / 24)
        val title = new Paragraph(text).setFont(font).setFontSize(9).setFontColor(color).setTextAlignment(TextAlignment.LEFT).setFixedLeading(16.24f).setFixedPosition(1, left, bottom, 206.5f)
        document.add(title)
        document.close()
        reader.close()
        writer.close()
        pdfDoc.close()
        FileUtils.copyFile(new File(outpath), new File(inpath))
      }

    }
  }

  //饮食
  def addDietToPdf(path: String, map: Map[String, String], sex: String, tmpPath: String): Unit = {
    val array = Array((348, 1), (349, 2))
    for (a <- array) {
      val page = if (sex == "men") {
        a._1
      } else {
        a._1 + 10
      }
      val inpath = tmpPath + "/mainTmp/" + page + ".pdf"
      val outpath = tmpPath + "/mainTmp2/" + page + ".pdf"
      val reader = new com.itextpdf.kernel.pdf.PdfReader(inpath)
      val writer = new PdfWriter(outpath)
      val pdfDoc = new PdfDocument(reader, writer)
      val document = new Document(pdfDoc)
      val font = PdfFontFactory.createFont(path + "/fonts/msyhbd.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      val color = new DeviceRgb(255, 241, 0)
      //setFixedLeading :设置行高
      val r1 = map.get("碳水化合物代谢水平").get
      val r2 = map.get("蛋白质代谢水平").get
      val r3 = map.get("脂质代谢水平").get

      def getNum(r: String): Int = {
        r match {
          case "OR大" => 1
          case "OR中" => 2
          case "OR小" => 3
        }
      }

      if (a._2 == 1) {
        val text = FileUtils.readFileToString(new File(path + "/suggest/corporeity/12/" + getNum(r1) + ".txt"), "UTF-8")
        val t = text.split("，").mkString("，\n")
        val title = new Paragraph(t).setFont(font).setFontSize(9).setFontColor(color).setTextAlignment(TextAlignment.CENTER).setFixedLeading(16.24f).setFixedPosition(1, 350f, 572f, 175f)
        document.add(title)
      } else {
        val text1 = FileUtils.readFileToString(new File(path + "/suggest/corporeity/12/" + (getNum(r2) + 3) + ".txt"), "UTF-8")
        val text2 = FileUtils.readFileToString(new File(path + "/suggest/corporeity/12/" + (getNum(r3) + 6) + ".txt"), "UTF-8")
        val t1 = text1.split("，").mkString("，\n")
        val t2 = text2.split("，").mkString("，\n")
        val title2 = new Paragraph(t1).setFont(font).setFontSize(9).setFontColor(color).setTextAlignment(TextAlignment.CENTER).setFixedLeading(16.24f).setFixedPosition(1, 697f, 572f, 160)
        val title3 = new Paragraph(t2).setFont(font).setFontSize(9).setFontColor(color).setTextAlignment(TextAlignment.CENTER).setFixedLeading(16.24f).setFixedPosition(1, 968f, 572f, 160)
        document.add(title2)
        document.add(title3)
      }
      document.close()
      reader.close()
      writer.close()
      pdfDoc.close()
      FileUtils.copyFile(new File(outpath), new File(inpath))
    }

  }
}
