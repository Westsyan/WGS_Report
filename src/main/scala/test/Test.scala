package test

import java.io.File

import org.apache.commons.io.FileUtils

object Test {

  def main(args: Array[String]): Unit = {
    val regex="^\\s*$".r
    val buf = FileUtils.readFileToString(new File("D:\\fandi\\data\\18YFQ0051/18YFQ0051.CNVreport.txt"))
    println(buf.matches("^\\s*$"))
    val rs=regex.findFirstIn(buf)
   println(rs.isDefined)
    println(rs)
  }

}
