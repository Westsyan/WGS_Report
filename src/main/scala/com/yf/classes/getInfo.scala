package com.yf.classes

import java.io.File
import java.util

import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import scala.util.control.Breaks._

object getInfo {


  def groupPage(dataPath: String, resPath: String, outpath: String, sex: String,tmpPath:String) = {

    val iconPath = resPath + "/icon/"
    val configurePath = resPath + "/configure"
    val pages = if (sex == "men") {
      386
    } else {
      396
    }

    val tumors = Array("肺癌", "胶质瘤", "胶质母细胞瘤", "垂体腺瘤", "神经母细胞瘤", "甲状腺癌", "黑色素瘤", "基底细胞癌",
      "鳞状细胞癌", "膀胱癌", "鼻咽癌", "喉癌", "口咽癌", "结直肠癌", "胆囊癌", "肝细胞癌", "食管鳞状细胞癌", "食管腺癌",
      "胃癌", "胰腺癌", "腹主动脉瘤", "颅内动脉瘤", "多发性骨髓瘤", "霍奇金淋巴瘤", "B细胞淋巴瘤", "滤泡性淋巴瘤",
      "急性淋巴细胞白血病", "慢性粒细胞白血病", "慢性淋巴细胞白血病", "前列腺癌", "睾丸癌", "乳腺癌",
      "子宫内膜癌", "宫颈癌", "卵巢癌")


//    val genoSummary = new File(dataPath).listFiles().filter(_.getName.contains("GenoSummary")).head
    val genoSummary = new File(dataPath + ".GenoSummary.txt")
    val data = FileUtils.readLines(genoSummary, "UTF-8").asScala
    val model = FileUtils.readLines(new File(configurePath + "/model.txt"), "UTF-8").asScala

    val data1 = data.map(_.split("\t")).map(x => ((x.head, x.last)))
    val model1 = model.map(_.split("\t")).map(x => (x.head, x.last))
    val map = new util.HashMap[String, Object]()

    data1.map { x =>
      val name = model1.filter(_._1 == x._1).map(_._2)
      (x._2, name)
    }.filter(_._2.size == 1).map { x =>
      map.put(x._2.head, x._1)
    }

//    val diseaseRisk = new File(dataPath).listFiles().filter(_.getName.contains("DiseaseRisk")).head
    val diseaseRisk = new File(dataPath + ".DiseaseRisk.txt")
    val data3 = FileUtils.readLines(diseaseRisk, "UTF-8").asScala

    val data4 = data3.map(_.split("\t"))

    val m = suggest.nameMap

    val map2 = new util.HashMap[String, Object]()


    val information = FileUtils.readLines(new File(dataPath + ".Information.txt"),"UTF-8").asScala

    map.put("name",information.head)
    map.put("sex",information(1))
    map.put("birthday",information(2))
    map.put("sampleid",information(3))
    map.put("program",information(4))
    map.put("phone",information(5))
    map.put("workunit",information(6))
    map.put("inunit",information.last)

    map2.put("qr",dataPath + ".QuickMark.png")

    data4.map { x =>
      val head = x.head.split("\uFEFF")
      val formName = m.get(head.last).get
      //     val formName = model4.filter(_.head == x.head).map(_.last).head
      map.put(formName + "1", x(1)) //风险倍数
      map.put(formName + "2", x(2)) //风险等级

      val name = x(2).split("").mkString(" / ")
      map.put(formName + "3", name) //风险等级标题

      /*
            val suggest = x(3).split("")
            val (suggest1, suggest2, suggest3) = if (suggest.size <= 23) {
              (suggest.mkString, "", "")
            } else if (suggest.size <= 46) {
              (suggest.take(23).mkString, suggest.drop(23).mkString.mkString, "")
            } else {
              (suggest.take(23).mkString, suggest.slice(23, 46).mkString, suggest.drop(46).mkString)
            }
            map.put(formName + "4", suggest1) //建议第一行
            map.put(formName + "5", suggest2) //建议第二行
            map.put(formName + "6", suggest3) //建议第三行

      */

      val (kind, risk) = if (tumors.contains(x(0))) {
        ("tumor", "tumorRisk")
      } else {
        ("illness", "illnessRisk")
      }
      val kindp = iconPath + kind
      val riskp = iconPath + risk
      val (imgPath, riskPath) = x(2) match {
        case "低风险" => (kindp + "_1.png", riskp + "_1.png")
        case "一般风险" => (kindp + "_2.png", riskp + "_2.png")
        case "中等风险" => (kindp + "_3.png", riskp + "_3.png")
        case "较高风险" => (kindp + "_4.png", riskp + "_4.png")
      }

      map2.put(formName + "risk", riskPath) //风险图片路径
      map2.put(formName + "img", imgPath) //图片路径
    }

  //  val drugEffect = new File(dataPath).listFiles().filter(_.getName.contains("DrugEffect")).head
    val drugEffect = new File(dataPath + ".DrugEffect.txt")
    val drug = FileUtils.readLines(drugEffect, "UTF-8").asScala
    val drugMap = drug.map(_.split("\t"))
    val d = suggest.drugMap
    drugMap.map { x =>
      val risk = x.last
      val head = x.head.split("\uFEFF").last
      val drugName = d.get(head).get
      val riskImg = risk match {
        case "疗效较弱" => iconPath + "drugRisk_1.png"
        case "疗效正常" => iconPath + "drugRisk_2.png"
        case "疗效较好" => iconPath + "drugRisk_3.png"
      }
      map.put(drugName + "1", risk)
      map2.put(drugName + "risk", riskImg)
    }

    val suggestTmp = new File(tmpPath + "/suggestTmp").listFiles().sorted

    for (i <- 16 to pages) {
      val page = i + suggestTmp.length - 7
      val pages = if (page < 100) {
        "0" + page.toString
      } else {
        page.toString
      }
      map.put(i.toString, pages)
    }

    val sexPage = sex match {
      case "men" => 0
      case "women" => 10
    }

    val cnvTmp = new File(tmpPath + "/cnvTmp2").listFiles().sorted
    for (i <- (334 + sexPage) to pages) {
      val page = i + cnvTmp.length + suggestTmp.length - 14
      map.put(i.toString, page.toString)
    }

    val crisk = FileUtils.readLines(new File(resPath + "/suggest/corporeity/risk.txt"),"UTF-8").asScala

  //  val physical = new File(dataPath).listFiles().filter(_.getName.contains("PhysicalCharacter")).head
    val physical = new File(dataPath + ".PhysicalCharacter.txt")
    val cin = FileUtils.readLines(physical,"UTF-8").asScala

    val ctoi = cin.map { x =>
      val r = x.split("\t")
      val index = r.last match {
        case "OR大" => 1
        case "OR中" => 2
        case "OR小" => 3
      }
      (r.head.split("\uFEFF").last, index)
    }

    crisk.map { x =>
      val r = x.split("\t")
      val corporHead = r.head.split("\uFEFF").last
      var index = 0
      var corpor = ""
      try {
        index = ctoi.filter(_._1 == corporHead).map(_._2).head
        corpor = suggest.corporietyMap.get(corporHead).get
      } catch {
        case e: Exception =>
          println("体质数据缺失： " + corporHead + "\n请添加后再次运行程序终止！")
          break()
      }
      map.put(corpor + "risk", r(index))
    }

    val o = new util.HashMap[String, Object]()
    o.put("datemap", map)
    o.put("imgmap", map2)
    for (i <- 1 to pages) {

      val fontpath = if (i < (pages - 110)) {
        resPath + "/fonts/msyh.ttc,1"
      } else {
        resPath + "/fonts/msyhbd.ttc,1"
      }

      com.yf.classes.PdfUtils.pdfout(o, i, fontpath, resPath, sex,tmpPath)
    }
    println("信息插入成功！")

    new File(tmpPath + "/mainTmp2").mkdir()

    val a = cin.map { x =>
      val q = x.split("\t")
      (q.head.split("\uFEFF").last, q.last)
    }

    cnvSuggest.addTextToPdf(resPath, a.toArray, sex,tmpPath)
    //饮食对体质的影响，需要特殊处理
    cnvSuggest.addDietToPdf(resPath, a.toMap, sex,tmpPath)
    println("体质处理成功！")

    val files = new Array[String](pages + suggestTmp.length + cnvTmp.length - 11)


    for (i <- 1 to 11) {
      files(i - 1) = tmpPath + "/mainTmp/" + i + ".pdf"
    }

    val suggestSize = 12 + suggestTmp.length

    for (i <- 12 until suggestSize) {
      val index = i - 11
      files(i - 1) = tmpPath + "/suggestTmp/" + index + ".pdf"
    }

    for (i <- 16 to (326 + sexPage)) {
      files(i - 5 + suggestTmp.length) = tmpPath + "/mainTmp/" + i + ".pdf"
    }

    for (i <- (327 + sexPage) until (327 + cnvTmp.length + sexPage)) {
      val index = i - 326 - sexPage
      files(i - 5 + suggestTmp.length) = tmpPath + "/cnvTmp2/" + index + ".pdf"
    }

    for (i <- (334 + sexPage) to pages) {
      files(i - 12 + suggestTmp.length + cnvTmp.length) = tmpPath + "/mainTmp/" + i + ".pdf"
    }

    println("页数：" + files.length)

    println("正在生成报告...")
    com.yf.classes.PdfUtils.mergePdfFiles(files, outpath)
  }


}
