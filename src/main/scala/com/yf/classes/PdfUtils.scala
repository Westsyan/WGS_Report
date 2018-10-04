package com.yf.classes

import java.io.{ByteArrayOutputStream, FileOutputStream, IOException}
import java.util


import com.itextpdf.text.pdf._
import com.itextpdf.text.{Document, DocumentException, Image, Rectangle}

object PdfUtils {

  //合并pdf文件
  def mergePdfFiles(files: Array[String], savepath: String): Unit = {
    try {
      val read = new PdfReader(files(0))
      val document = new Document(read.getPageSize(1))
      val copy = new PdfCopy(document, new FileOutputStream(savepath))
      document.open()
      for (i <- 0 until files.size) {
        val reader = new PdfReader(files(i))
        val n = reader.getNumberOfPages
        for (j <- 1 to n) {
          document.newPage
          val page = copy.getImportedPage(reader, j)
          copy.addPage(page)
        }
        reader.close()
      }
      document.close()
      read.close()
    } catch {
      case e: IOException =>
        e.printStackTrace()
      case e: DocumentException =>
        e.printStackTrace()
    }
  }

  // 利用模板生成pdf
  def pdfout(o: util.Map[String, AnyRef], z: Int, fontPath: String, path: String, sex: String,tmpPath:String): Unit = { // 模板路径
    val templatePath: String = path + "/model/" + sex + "/" + sex + " " + z + ".pdf"
    var reader: PdfReader = null
    var out: FileOutputStream = null
    var bos: ByteArrayOutputStream = null
    var stamper: PdfStamper = null
    val canvas: PdfContentByte = null
    try {
      val bf: BaseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      val bf1: BaseFont = BaseFont.createFont(path + "/fonts/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      //   Font FontChinese = new Font(bf, 5, Font.NORMAL);
      reader = new PdfReader(templatePath) // 读取pdf模板

      bos = new ByteArrayOutputStream
      stamper = new PdfStamper(reader, bos)
      val form: AcroFields = stamper.getAcroFields
      //文字类的内容处理
      val datemap: util.Map[String, String] = o.get("datemap").asInstanceOf[util.Map[String, String]]
      form.addSubstitutionFont(bf1)
      form.addSubstitutionFont(bf)
      import scala.collection.JavaConversions._
      for (key <- datemap.keySet) {
        val value: String = datemap.get(key)
        //       String[] a = key.split("_");
        /*                if (a[1].equals("2") || a[1].equals("3")) {
                            form.addSubstitutionFont(bf);
                            form.setField(key, value);
                        } else {*/ form.addSubstitutionFont(bf1)
        form.setField(key, value)
        //    }
        // form.setField(key, value);
      }
      if (o.get("imgmap") != null) {
        //图片类的内容处理
        val imgmap: util.Map[String, String] = o.get("imgmap").asInstanceOf[util.Map[String, String]]
        import scala.collection.JavaConversions._
        for (key <- imgmap.keySet) {
          val value: String = imgmap.get(key)
          val imgpath: String = value
          //   int pageNo = form.getFieldPositions(key).get(0).page;
          if (form.getFieldPositions(key) != null) {
            val signRect: Rectangle = form.getFieldPositions(key).get(0).position
            val x: Float = signRect.getLeft
            val y: Float = signRect.getBottom
            //根据路径读取图片
            val image: Image = Image.getInstance(imgpath)
            //获取图片页面
            val under: PdfContentByte = stamper.getOverContent(1)
            //     PdfContentByte under = stamper.getOverContent(pageNo);
            //图片大小自适应
            image.scaleToFit(signRect.getWidth, signRect.getHeight)
            //添加图片
            image.setAbsolutePosition(x, y)
            under.addImage(image)
          }
        }
      }
      stamper.setFormFlattening(true) // 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
      stamper.close()
      val doc: Document = new Document
      out = new FileOutputStream(tmpPath + "/mainTmp/" + z + ".pdf") // 输出流
      //  Font font = new Font(bf, 32);
      val copy: PdfCopy = new PdfCopy(doc, out)
      doc.open()
      var importPage: PdfImportedPage = null
      importPage = copy.getImportedPage(new PdfReader(bos.toByteArray), 1)
      copy.addPage(importPage)
      doc.close()
      reader.close()
    } catch {
      case e: IOException =>
        System.out.println(e)
      case e: DocumentException =>
        System.out.println(e)
    }
  }


  // 利用模板生成pdf
  def pdfoutWithText(o: util.Map[String, AnyRef],  fontPath: String,inpath:String,outpath:String,resPath:String): Unit = { // 模板路径

    var reader: PdfReader = null
    var out: FileOutputStream = null
    var bos: ByteArrayOutputStream = null
    var stamper: PdfStamper = null
    val canvas: PdfContentByte = null
    try {
      val bf: BaseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      val bf1: BaseFont = BaseFont.createFont(resPath +  "/fonts/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      //   Font FontChinese = new Font(bf, 5, Font.NORMAL);
      reader = new PdfReader(inpath) // 读取pdf模板
      bos = new ByteArrayOutputStream
      stamper = new PdfStamper(reader, bos)
      val form: AcroFields = stamper.getAcroFields
      //文字类的内容处理
      val datemap: util.Map[String, String] = o.get("datemap").asInstanceOf[util.Map[String, String]]
      form.addSubstitutionFont(bf1)
      form.addSubstitutionFont(bf)
      import scala.collection.JavaConversions._
      for (key <- datemap.keySet) {
        val value: String = datemap.get(key)
        form.setField(key, value)
      }
      stamper.setFormFlattening(true) // 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
      stamper.close()
      val doc: Document = new Document
      out = new FileOutputStream(outpath) // 输出流
      val copy: PdfCopy = new PdfCopy(doc, out)
      doc.open()
      var importPage: PdfImportedPage = null
      importPage = copy.getImportedPage(new PdfReader(bos.toByteArray), 1)
      copy.addPage(importPage)
      doc.close()
      reader.close()
    } catch {
      case e: IOException =>
        System.out.println(e)
      case e: DocumentException =>
        System.out.println(e)
    }
  }


}
