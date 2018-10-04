package com.yf.classes

import java.io.{File, FileOutputStream}
import java.util

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.text.pdf.parser.{ImageRenderInfo, PdfReaderContentParser, RenderListener, TextRenderInfo}
import com.itextpdf.text.pdf.{BaseFont, PdfReader, PdfStamper}
import com.itextpdf.text.{BaseColor, Rectangle}
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

object cnvMain {

  def addCNVSuggestToPdf(dataPath: String, resPath: String,sex:String,tmpPath:String) = {

    val file = FileUtils.readLines(new File(resPath + "/suggest/cnv/cnvSuggest.txt"), "UTF-8").asScala

    val suggestFiles = new File(tmpPath + "/suggestTmp").listFiles()
    val pagi = sex match{
      case "men" => 326
      case "women" => 336
    }

    val pagis = suggestFiles.size + pagi -7

    val suggest = file.map(_.split("\t"))

   // val reportfile = new File(dataPath).listFiles().filter(_.getName.contains("CNVreport")).head
    val reportfile = new File(dataPath + ".CNVreport.txt")
    val cnvreport = FileUtils.readLines(reportfile, "UTF-8").asScala


    new File(tmpPath + "/cnvTmp1").mkdir()
    new File(tmpPath + "/cnvTmp2").mkdir()

    val map = new util.HashMap[String, Object]()

    val array = cnvreport.map { x =>
      val column = x.split("\t")
      val cnvNums = cnvreport.indexOf(x) + 1
      column.map { element =>
        map.put("cnv-" + cnvNums + "_" + (column.indexOf(element) + 1), element)
      }
      column.last
    }
    map.put("1",(pagis+1).toString)
    val o = new util.HashMap[String, Object]()
    o.put("datemap", map)

    PdfUtils.pdfoutWithText(o, resPath + "/fonts/msyhbd.ttc,1", resPath + "/model/cnv/" + cnvreport.size + ".pdf", tmpPath + "/cnvTmp2/1.pdf",resPath)

    val black = new DeviceRgb(62, 62, 62)
    val white = new DeviceRgb(255, 255, 255)
    val purple = new DeviceRgb(113, 63, 151)

    var count = 2
    var side = 1

    array.flatMap { x =>
      val cnv = x.split(";")
      cnv.grouped(2).map { ill =>
        var position = Array[(String, Float, Float, DeviceRgb, Int, Int)]()

        val cnv1 = ill.head.split(",")
        val row1 = suggest.filter(x => x(1) == cnv1(1)).head

        position = position :+ (s"项目名称：${row1.head} \t\t 表型名称：${row1(1)} \t\t 风险：${cnv1.last}", 68.3574f, 564.83f, white, 10, 2)
        val title1 = 564.83f - 39.4727f
        position = position :+ ("表型简介", 39.3721f, title1, purple, 10, 2)
        val line1 = title1 - 6.5f
        var coord = Array((line1, 39.255f, 555.255f, 2f, new BaseColor(62, 62, 62)))

        val text1 = title1 - 26f - 14.694f * (row1(2).toInt - 1)
        position = position :+ (row1(4), 39.255f, text1, black, 9, 1)

        val title2 = text1 - 28f
        position = position :+ ("健康建议", 39.3721f, title2, purple, 10, 2)
        val line2 = title2 - 6.5f
        coord = coord :+ (line2, 39.255f, 555.255f, 2f, new BaseColor(62, 62, 62))

        coord = coord :+ (39.4f, 586.77f, 53.57f, 558.45f, new BaseColor(246, 147, 34))
        coord = coord :+ (53.57f, 586.77f, 555.13f, 558.45f, new BaseColor(113, 63, 151))

        val text2 = title2 - 26f - 14.694f * (row1(3).toInt - 1)

        position = position :+ (row1(5), 39.255f, text2, black, 9, 1)

        val cnv2 = ill.last.split(",")
        val row2 = suggest.filter(x => x(1) == cnv2(1)).head
        val t2 = text2 - 45f
        if(ill.size == 2) {

          position = position :+ (s"项目名称：${row2.head} \t\t 表型名称：${row2(1)} \t\t 风险：${cnv2.last}", 68.3574f, t2, white, 10, 2)

          coord = coord :+ (39.4f, t2 + 21.94f, 53.57f, t2 - 6.4f, new BaseColor(246, 147, 34))
          coord = coord :+ (53.57f, t2 + 21.94f, 555.13f, t2 - 6.4f, new BaseColor(113, 63, 151))
          val title3 = t2 - 39.4727f
          position = position :+ ("表型简介", 39.3721f, title3, purple, 10, 2)
          val line3 = title3 - 6.5f
          coord = coord :+ (line3, 39.255f, 555.255f, 2f, new BaseColor(62, 62, 62))

          val text3 = title3 - 26f - 14.694f * (row2(2).toInt - 1)
          position = position :+ (row2(4), 39.255f, text3, black, 9, 1)

          val title4 = text3 - 28f
          position = position :+ ("健康建议", 39.3721f, title4, purple, 10, 2)
          val line4 = title4 - 6.5f
          coord = coord :+ (line4, 39.255f, 555.255f, 2f, new BaseColor(62, 62, 62))

          val text4 = title4 - 26f - 14.694f * (row2(3).toInt - 1)

          position = position :+ (row2(5), 39.255f, text4, black, 9, 1)

        }
        val page = side match {
          case 1 => 36.22f
          case 2 => 530f
        }

        side = side match {
          case 1 => 2
          case 2 => 1
        }

        position = position :+ ("Page "+(pagis+count), page, 32.85f, new DeviceRgb(146, 149, 151), 7, 3)

        addElementToPdf(resPath + "/model/cnv/cnv-" + (array.indexOf(x) + 1) + ".pdf", tmpPath + "/cnvTmp1/" + count + ".pdf", coord, 2)
        addContentToPdf(tmpPath + "/cnvTmp1/" + count + ".pdf", tmpPath + "/cnvTmp2/" + count + ".pdf", position, 2,resPath)
        count += 1
      }
    }

    side = addCNVModelToPdf(count, side, resPath, pagis,tmpPath)

    if (side == 2) {
      FileUtils.copyFile(new File(resPath + "/model/nullPage.pdf"), new File(tmpPath + "/cnvTmp2/" + (count + 4) + ".pdf"))
    }
  }

  def addCNVModelToPdf(count: Int, sides: Int, resPath: String, pages: Int,tmpPath:String): Int = {
    var side = sides
    for (i <- count until (count + 4)) {
      val page = side match {
        case 1 => 36.22f
        case 2 => 540f
      }
      val position = Array(("Page " + (pages + i-1), page, 32.85f, new DeviceRgb(146, 149, 151), 7, 3))
      addContentToPdf(resPath + "/model/cnv/" + (i - count + 5) + "_men.pdf", tmpPath + "/cnvTmp2/" + i + ".pdf", position, 2,resPath)
      side = side match {
        case 1 => 2
        case 2 => 1
      }
    }
    side
  }

  def noneCnv(resPath: String, sex: String,tmpPath:String) = {
    val suggestFile = new File(tmpPath + "/suggestTmp").listFiles()
    val sexPage = sex match {
      case "men" => 0
      case "women" => 10
    }
    val page = 326 + suggestFile.size + sexPage -6
    val position = Array(("Page " + page, 1140f, 32.85f, new DeviceRgb(146, 149, 151), 7, 3))
    addContentToPdf(resPath + "/model/cnv/cnv_null.pdf", tmpPath + "/cnvTmp2/1.pdf", position, 2,resPath)
    addCNVModelToPdf(2, 1, resPath, page,tmpPath)
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
        //线的颜色
        over.setColorStroke(c._5)
        //虚线
        over.setLineDash(2f, 0.1f)
        //线的粗度
        over.setLineWidth(0.3f)
        //起始坐标
        over.moveTo(c._2, c._1)
        //终止坐标
        over.lineTo(c._3, c._1)
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

  def getKeyWords(file: String, keyword: String): Array[Float] = {
    val pdfReader = new PdfReader(file)
    val pdfReaderContentParser = new PdfReaderContentParser(pdfReader)

    var flo = new Array[Float](0)
    val li = pdfReaderContentParser.processContent(1, new RenderListener {
      override def renderImage(renderInfo: ImageRenderInfo): Unit = {}

      override def endTextBlock(): Unit = {}

      override def renderText(renderInfo: TextRenderInfo) = {
        val text = renderInfo.getText
        if (text != null && text.contains(keyword)) {
          val boundingRectange = renderInfo.getBaseline.getBoundingRectange
          flo = new Array[Float](2)
          flo(0) = boundingRectange.x
          flo(1) = boundingRectange.y
        }
      }

      override def beginTextBlock(): Unit = {}
    })

    flo
  }

  def addContentToPdf(inpath: String, outpath: String, position: Array[(String, Float, Float, DeviceRgb, Int, Int)], side: Int,resPath:String): Unit = {
    val reader = new com.itextpdf.kernel.pdf.PdfReader(inpath)
    val writer = new PdfWriter(outpath)
    val pdfDoc = new PdfDocument(reader, writer)
    val document = new Document(pdfDoc)
    val width = 515
    for (p <- position) {
      val font = p._6 match {
        case 1 => PdfFontFactory.createFont(resPath + "/fonts/msyh.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        case 2 => PdfFontFactory.createFont(resPath + "/fonts/msyhbd.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        case 3 => PdfFontFactory.createFont(resPath + "/fonts/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      }
      val title = new Paragraph(p._1).setFont(font).setFontSize(p._5).setFontColor(p._4).setTextAlignment(TextAlignment.JUSTIFIED).setFixedLeading(14.694f).setFixedPosition(1, p._2, p._3, width)
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
}
