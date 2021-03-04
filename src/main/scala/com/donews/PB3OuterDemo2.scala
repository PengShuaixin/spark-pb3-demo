package com.donews

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @title PB3OuterDemo
 * @projectName spark-pb3-demo
 * @author Niclas
 * @date 2021/2/26 15:02
 * @description
 */
object PB3OuterDemo2 {
  /*
  spark-submit --class com.donews.PB3OuterDemo --master yarn-client --driver-memory 1g --driver-cores 1 --executor-memory 1g --executor-cores 1 --num-executors 1 --queue root.default --conf spark.dynamicAllocation.enabled=false spark-pb3-demo-1.0-SNAPSHOT.jar
   */
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .set("spark.streaming.stopGracefullyOnShutdown", "true")
      .set("spark.streaming.backpressure.enabled", "true")
      .setAppName(this.getClass.getSimpleName)
      .setMaster("local[*]")

    val ssc = new StreamingContext(conf, Seconds(3))

    val kafkaSourceParams = Map[String, Object](
      "bootstrap.servers" -> "bjxg-bd-slave01:29092,bjxg-bd-slave02:29092,bjxg-bd-slave03:29092,bjxg-bd-slave04:29092,bjxg-bd-slave05:29092",
      "group.id" -> "pb_test",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[UserInfoDeserializer],
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val sourceTopic = Array("common-user-info")

    val stream = KafkaUtils.createDirectStream[String, UserInfoBuf.UserInfo](
      ssc,
      PreferConsistent,
      Subscribe[String, UserInfoBuf.UserInfo](sourceTopic, kafkaSourceParams)
    )

    stream.foreachRDD(rdd => {
      // 获取偏移量
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.map(cr =>{
        val userInfo = cr.value()
        userInfo
      }).foreach(println)

      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
