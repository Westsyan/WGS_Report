package com.yf.classes

import java.io.{File, FileOutputStream}
import java.util

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.{PdfDocument, PdfReader, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.{BaseFont, PdfStamper}
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

object illSuggest {


  def dealWithDisease(dataPath: String,resPath:String,sex:String,tmpPath:String) : Int = {
    val file = FileUtils.readLines(new File(resPath +  "/configure/line.txt"),"UTF-8").asScala
  //  val diseaseRisk = new File(dataPath).listFiles().filter(_.getName.contains("DiseaseRisk")).head
    val diseaseRisk = new File(dataPath + ".DiseaseRisk.txt")
    val ill = FileUtils.readLines(diseaseRisk,"UTF-8").asScala
    val ills = ill.map { x =>
      val column = x.split("\t")
      (column.head.split("\uFEFF").last, column.last)
    }
    val name1 = ills.filter(_._2 == "较高风险" ).map(_._1)
    val name2 = ills.filter(_._2 == "中等风险").map(_._1)
    var name = name1 ++ name2

    val lineMap = file.map { x =>
      val l = x.split("\t")
      (l.head.split("\uFEFF").last, l(1))
    }.toMap
    var high1 = 616.54f
    var side = 1
    var position = new Array[(String, Float, Float)](0)
    var text = 100f
    var count = 0
    var high = new Array[Float](0)
    while (name.length > 0) {
      val title = high1 - 18.01f
      val high2 = high1 - 19.76f
      val line = lineMap.get(name.head)
      text = high2 - 22.6f - 16.24f * (line.get.toInt - 1)
      if (text > 49f) {
        high = high ++ Array(high1, high2)
        position = position :+ (name.head, title, text)
        name = name.drop(1)
        high1 = text - 19.3f
      } else {
        for (n <- name) {
          val titleBottom = high1 - 18.01f
          val high2Bottom = high1 - 19.76f
          val lineBottom = lineMap.get(n)
          text = high2Bottom - 22.6f - 16.24f * (lineBottom.get.toInt - 1)
          if (text > 49f) {
            high = high ++ Array(high1, high2Bottom)
            position = position :+ (n, titleBottom, text)
            name = name.diff(Array(n))
            high1 = text - 19.3f
          }
        }
        count += 1
        side = addSuggestToPdf(count, position, side, side, high, resPath, resPath + "/model/" + sex + "/"+sex+" 12.pdf",sex,1,tmpPath,dataPath)._1
        high1 = 701.72f
        high = new Array[Float](0)
        position = new Array[(String, Float, Float)](0)
      }
    }
    count += 1
    side = addSuggestToPdf(count, position, side, side, high, resPath, resPath + "/model/" + sex + "/"++sex+" 12.pdf",sex,1,tmpPath,dataPath)._1
    var counts = count
    if (side == 2) {
      FileUtils.copyFile(new File(resPath + "/model/nullPage.pdf"), new File(tmpPath + "/suggestTmp/" + (count + 1) + ".pdf"))
      counts = count + 1
    }
    counts
  }

  def addSuggestToPdf(count: Int, position: Array[(String, Float, Float)], side: Int, sides: Int, high: Array[Float], path: String, inpath: String,sex:String,dataType:Int,tmpPath:String,dataPath:String): (Int, String) = {
    var s = 0
    val map = new util.HashMap[String, Object]()
    val o = new util.HashMap[String, Object]()
    val file1 = tmpPath + "/suggestTmp/" + count + ".pdf"
    val file2 = tmpPath + "/suggestTmp1/" + count + ".pdf"
    val page = dataType match {
      case 1 => "0" + (count + 8).toString
      case 2 => "0" + (count + 7).toString
    }
    if (side == 1) {
      val information = FileUtils.readLines(new File(dataPath + ".Information.txt"),"UTF-8").asScala
      map.put("name", information.head)
      o.put("datemap", map)
      PdfUtils.pdfoutWithText(o, path +"/fonts/msyhbd.ttc,1", inpath, file1,path)
      addContentToPdf(file1, file2, position, path,sides)
      addElementToPdf(file2, file1, high, sides)
      s = 2
    } else if (side == 2) {
      map.put("13", page)
      o.put("datemap", map)
      PdfUtils.pdfoutWithText(o, path +"/fonts/msyh.ttc,1", path + "/model/" + sex + "/"+ sex +" 14.pdf", file1,path)
      addContentToPdf(file1, file2, position, path,side)
      addElementToPdf(file2, file1, high, side)
      s = 3
    } else {
      map.put("12", page)
      o.put("datemap", map)
      PdfUtils.pdfoutWithText(o, path + "/fonts/msyh.ttc,1",path + "/model/" + sex + "/"+ sex + " 13.pdf", file1,path)
      addContentToPdf(file1, file2, position, path,side)
      addElementToPdf(file2, file1, high, side)
      s = 2
    }
    (s, tmpPath + "/suggestTmp/" + count + ".pdf")
  }

  def addContentToPdf(inpath: String, outpath: String, position: Array[(String, Float, Float)],resPath:String, side: Int): Unit = {
    val reader = new PdfReader(inpath)
    val writer = new PdfWriter(outpath)
    val pdfDoc = new PdfDocument(reader, writer)
    val document = new Document(pdfDoc)
    val font = PdfFontFactory.createFont(resPath + "/fonts/msyh.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
    val line = FileUtils.readLines(new File(resPath + "/configure/line.txt"),"UTF-8").asScala
    val fileMap = line.map{x=>
      val col = x.split("\t")
      (col.head.split("\uFEFF").last, col.last)
    }.toMap
    //单位应该是px
    val (left, titleLeft) = if (side == 1 || side == 3) {
      (127.5f, 128f)
    } else {
      (649.5f, 643.7f)
    }
    val width = 430
    val color = new DeviceRgb(62, 62, 62)
    //setFixedLeading :设置行高
    for (p <- position) {
      val suggestTxt = FileUtils.readFileToString(new File(resPath + "/suggest/disease/" + fileMap.get(p._1).get + ".txt"),"UTF-8")
      val title = new Paragraph(p._1).setFont(font).setFontSize(12).setFontColor(color).setTextAlignment(TextAlignment.CENTER).setFixedLeading(16.24f).setFixedPosition(1, titleLeft, p._2, width)
      val text = new Paragraph(suggestTxt).setTextAlignment(TextAlignment.JUSTIFIED).setFont(font).setFontSize(9).setFontColor(color).setFixedLeading(16.24f).setFixedPosition(1, left, p._3, width)
      document.add(title)
      document.add(text)
    }
    document.close()
    pdfDoc.close()
    writer.close()
    reader.close()
  }

  def addElementToPdf(inpath: String, outpath: String, high: Array[Float], side: Int): Unit = {
    val reader = new com.itextpdf.text.pdf.PdfReader(inpath)
    val stamper = new PdfStamper(reader, new FileOutputStream(outpath))
    val over = stamper.getOverContent(1)
    val (x1, x2) = if (side == 1 || side == 3) {
      (127.55f, 558.38f)
    } else {
      (649.07f, 1080.02f)
    }
    over.setColorStroke(new BaseColor(62, 62, 63))
    //线的粗度
    over.setLineWidth(0.3f)
    for (y <- high) {
      //起始坐标
      over.moveTo(x1, y)
      //终止坐标
      over.lineTo(x2, y)
      over.stroke()
    }
    stamper.close()
    reader.close()
  }

  def addCnvToPDF(resPath: String, cnv: Array[String], head: Float, page: Int, sides: Int,sex:String,tmpPath:String,dataPath:String): (Int, Int) = {
    val inpath = tmpPath + "/suggestTmp/" + page + ".pdf"
    val file = FileUtils.readLines(new File(resPath +  "/configure/line.txt"),"UTF-8").asScala
    val lineMap = file.map { x =>
      val l = x.split("\t")
      (l.head, l(1))
    }.toMap
    var name = cnv.distinct
    var high1 = head
    var side = 1
    var position = new Array[(String, Float, Float)](0)
    var text = 100f
    var count = page
    var high = new Array[Float](0)
    while (name.length > 0) {
      val title = high1 - 18.01f
      val high2 = high1 - 19.76f
      val line = lineMap.get(name.head)
      text = high2 - 22.6f - 16.24f * (line.get.toInt - 1)
      if (text > 49f) {
        high = high ++ Array(high1, high2)
        position = position :+ (name.head, title, text)
        name = name.drop(1)
        high1 = text - 19.3f
      } else {
        for (n <- name) {
          val titleBottom = high1 - 18.01f
          val high2Bottom = high1 - 19.76f
          val lineBottom = lineMap.get(n)
          text = high2Bottom - 22.6f - 16.24f * (lineBottom.get.toInt - 1)
          if (text > 49f) {
            high = high ++ Array(high1, high2Bottom)
            position = position :+ (n, titleBottom, text)
            name = name.diff(Array(n))
            high1 = text - 19.3f
          }
        }
        count += 1
        side = addSuggestToPdf(count, position, side, sides, high, resPath, inpath,sex,2,tmpPath,dataPath)._1
        high1 = 701.72f
        high = new Array[Float](0)
        position = new Array[(String, Float, Float)](0)
      }
    }
    count += 1
    val po = addSuggestToPdf(count, position, side, side, high, resPath, inpath,sex,2,tmpPath,dataPath)
    side = po._1
    for (i <- page until count) {
      FileUtils.copyFile(new File(tmpPath + "/suggestTmp/" + (i+1) + ".pdf"), new File(tmpPath + "/suggestTmp1/" + (i+10) + ".pdf"))
    }
    for (i <- page until count) {
      FileUtils.copyFile(new File(tmpPath + "/suggestTmp1/" + (i+10) + ".pdf"), new File(tmpPath + "/suggestTmp/" + i  + ".pdf"))
    }
    (side, count)
  }
}
