# solr-data-export

读取 lucene 索引文件，solr 全量数据导出。

优点：
1. 无需访问 solr 集群服务。solr 停用也可执行导出，solr 在用不影响线上服务。
2. 文件读取，速度快。
3. 全量数据导出，包括 stored fields 和 docvalues。

其他导出方式存在的问题：
1. solr 分页查询，深度分页无法完成查询。
2. solr cursorMark 游标查询，需要排序字段有唯一值，且有排序开销。
3. solr /export RequestHandler，仅能导出 `docValue=true` 的字段。


目前只写了个 demo 自己导出用，hardcode 了字段名，有需要的自行参考写法，欢迎交流。

导出参考数据：索引 42 亿文档 TB 级别，单分片 4.2 亿文档 100GB 导出耗时 4 小时，可多分片并行。

lucene版本：6.6.2，不同版本使用的类方法会有差异。

暂作记录，待有空再补充这块。
