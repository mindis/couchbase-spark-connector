/**
 * Copyright (C) 2015 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */
package com.couchbase.spark.connection

import org.apache.spark.{SparkConf, SparkContext}

case class CouchbaseBucket(name: String, password: String)
case class CouchbaseConfig(hosts: Seq[String], buckets: Seq[CouchbaseBucket])

object CouchbaseConfig {

  private val PREFIX = "com.couchbase."
  private val BUCKET_PREFIX = PREFIX + "bucket."
  private val NODES_PREFIX = PREFIX + "nodes"

  val DEFAULT_NODE = "127.0.0.1"
  val DEFAULT_BUCKET = "default"
  val DEFAULT_PASSWORD = ""

  def apply(cfg: SparkConf) = {
    val bucketConfigs = cfg
      .getAll.to[List]
      .filter(pair => pair._1.startsWith(BUCKET_PREFIX))
      .map(pair => CouchbaseBucket(pair._1.replace(BUCKET_PREFIX, ""), pair._2))

    val nodes = cfg.get(NODES_PREFIX, DEFAULT_NODE).split(";")

    if (bucketConfigs.isEmpty) {
      new CouchbaseConfig(nodes, Seq(CouchbaseBucket(DEFAULT_BUCKET, DEFAULT_PASSWORD)))
    } else {
      new CouchbaseConfig(nodes, bucketConfigs)
    }
  }

  def apply() = new CouchbaseConfig(Seq(DEFAULT_NODE), Seq(CouchbaseBucket(DEFAULT_BUCKET, DEFAULT_PASSWORD)))

}
