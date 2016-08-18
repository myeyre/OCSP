package com.asiainfo.ocdp.local.shaanxiyidong.label

import com.asiainfo.ocdp.stream.common.StreamingCache
import com.asiainfo.ocdp.stream.constant.LabelConstant
import com.asiainfo.ocdp.stream.label.Label
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by gengwang on 16/8/11.
  */
class AreaLabel extends Label {
  val logger = LoggerFactory.getLogger(this.getClass)
  //区域标签前缀
  val tour_type_sine = "tour_area"
  val security_type_sine = "security_area"

  override def attachLabel(line: Map[String, String], cache: StreamingCache, labelQryData: mutable.Map[String, mutable.Map[String, String]]): (Map[String, String], StreamingCache) = {

    val labelMap = fieldsMap()

    val cachedArea = labelQryData.getOrElse(getQryKeys(line).head, Map[String, String]())

    labelMap += (LabelConstant.LABEL_TOUR_AREA -> "")
    labelMap += (LabelConstant.LABEL_SECURITY_TYPE -> "")

    // 标记业务区域标签： 如果codis中，存在item为areas的字段，则取其相关区域

   if (cachedArea.contains(tour_type_sine)) {
      // 从codis中取区域
      val area = cachedArea(tour_type_sine).trim()
      if (area != null && area != "") {
        // 信令所在区域列表
        labelMap += (LabelConstant.LABEL_TOUR_AREA -> area)
      }
    }

    // 标记安防标签
    if (cachedArea.contains(security_type_sine)) {
      // 从codis中取区域
      val area = cachedArea(security_type_sine).trim()
      if (area != null && area != "") {
        labelMap += (LabelConstant.LABEL_SECURITY_TYPE -> area)
      }
    }


    labelMap ++= line

    (labelMap.toMap, cache)

  }


  /**
    * 把cache的数据转为可变map
    */
  private def transformCacheMap2mutableMap(cacheInfo: Map[String, Map[String, String]]) = {
    val labelsPropMap = mutable.Map[String, mutable.Map[String, String]]()
    cacheInfo.map(infoMap => {
      val copProp = mutable.Map[String, String]()
      infoMap._2.foreach(copProp += _)
      labelsPropMap += (infoMap._1 -> copProp)
    })
    labelsPropMap
  }

  /**
    * 编辑完chache中的内容后重新置为不可变类属
    */
  private def transformCacheMap2ImmutableMap(labelsPropMap: mutable.Map[String, mutable.Map[String, String]]) = {
    if (labelsPropMap.isEmpty) Map[String, Map[String, String]]() else labelsPropMap.map(propSet => (propSet._1, propSet._2.toMap)).toMap
  }

  /**
    * @param line:MC信令对像
    * @return codis数据库的key
    */
  override def getQryKeys(line: Map[String, String]): Set[String] = Set[String]("area_info_:" + line("lac") + "_" + line("cell"))

}