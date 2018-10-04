package com.yf.APP

import java.io.File

import com.yf.classes.{cnvMain, cnvSuggest, getInfo}
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

object run {

  def runExec(dataPath: String, outpath: String) = {

    //  val dataPath ="D:/fandi/data/18719YHX"
    val path = this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath
    val p = path.split("/").map(_.trim).dropRight(1).mkString("/")
    val pa = {
      if (new File("C:/").exists()) p.drop(1) else p
    }

    val resPath = pa + "/resource"

    val information = FileUtils.readLines(new File(dataPath + ".Information.txt"),"UTF-8").asScala

    val sex = information(1) match {
      case "男" => "men"
      case "女" => "women"
    }
    var tmpPath = resPath + "/tmp/" + random

    new File(tmpPath).mkdir()
    new File(tmpPath + "/suggestTmp").mkdir()
    new File(tmpPath + "/suggestTmp1").mkdir()
    new File(tmpPath + "/mainTmp").mkdir()
    new File(tmpPath + "/mainTmp1").mkdir()
    new File(tmpPath + "/cnvTmp2").mkdir()
    new File(tmpPath + "/cnvTmp1").mkdir()

    println("正在处理癌症与疾病信息...")
    val count = com.yf.classes.illSuggest.dealWithDisease(dataPath, resPath, sex, tmpPath)

    println("正在处理CNV信息...")
    cnvSuggest.dealWithCnv(dataPath, resPath, count, sex, tmpPath)

    //    val reportfile = new File(dataPath).listFiles().filter(_.getName.contains("CNVreport")).head
    val reportfile = new File(dataPath + ".CNVreport.txt")
    val cnvreport = FileUtils.readFileToString(reportfile, "UTF-8")

    if (cnvreport.matches("^\\s*$")) {
      cnvMain.noneCnv(resPath, sex, tmpPath)
    } else {
      cnvMain.addCNVSuggestToPdf(dataPath, resPath, sex, tmpPath)
    }

    println("正在生成报告模板...")
    getInfo.groupPage(dataPath, resPath, outpath, sex, tmpPath)

    FileUtils.deleteDirectory(new File(tmpPath))
    println("报告生成成功！")


  }

  def random: String = {
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
    var value = ""
    for (i <- 0 to 20) {
      val ran = Math.random() * 62
      val char = source.charAt(ran.toInt)
      value += char
    }
    value
  }

  def main(args: Array[String]): Unit = {


    val usage =
      """
        |用法：  java -jar  jarfiles  -i xx -o xx
        |注意：
        |   请按顺序输入，搞错顺序程序将不能正确运行
        |
        |其中选项包括：
        |
        |   -i      inputPath:输入源文件路径
        |   -o      outputPath:输出报告路径
        |   -c      查看输入目录结构
      """.stripMargin
    val log =
      """
        | 数据目录结构：
        |
        |   -      ***.Information.txt                用户信息
        |   -      ***.QuickMark.png                  二维码
        |   -      ***.CNVreport.txt                  CNV信息
        |   -      ***.DiseaseRisk.txt                疾病与癌症风险
        |   -      ***.DrugEffect.txt                 药品疗效
        |   -      ***.GenoSummary.txt                基因位点
        |   -      ***.PhysicalCharacter.txt          体质信息
        |   注意：若目录名不一致或缺失目录中的文件，程序将不能正常运行！
      """.stripMargin
    val symbol = Array[String]("-i", "-o", "-c")
    if (args.length == 1 && args(0) == "-c") {
      println(log)
    } else if (args.length != 4) {
      println("输入参数缺失，请查看相关帮助：" + usage)
    } else {
      val (inPath, outPath) = (new File(args(1)), new File(args(3)))
      val a = Array(args(0), args(2))

      if (symbol.diff(a).size != 1) {
        println("输入选项有误，请查看相关帮助：" + usage)
      } else if (!new File(args(1) + ".CNVreport.txt").exists()) {
        println(args(1) + ".CNVreport.txt 不存在")
      } else if (!new File(args(1) + ".DiseaseRisk.txt").exists()) {
        println(args(1) + ".DiseaseRisk.txt 不存在")
      } else if (!new File(args(1) + ".DrugEffect.txt").exists()) {
        println(args(1) + ".DrugEffect.txt 不存在")
      } else if (!new File(args(1) + ".GenoSummary.txt").exists()) {
        println(args(1) + ".GenoSummary.txt 不存在")
      } else if (!new File(args(1) + ".PhysicalCharacter.txt").exists()) {
        println(args(1) + ".PhysicalCharacter.txt 不存在")
      } else if (!new File(args(1) + ".Information.txt").exists()) {
        println(args(1) + ".Information.txt 不存在")
      } else if (!new File(args(1) + ".QuickMark.png").exists()) {
        println(args(1) + ".QuickMark.png 不存在")
      } else {
        runExec(inPath.getPath, outPath.getPath)
      }
    }
  }
}
