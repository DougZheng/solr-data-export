# solr-data-export

读取lucene索引文件，solr全量数据导出。

优点：
1. 无需访问solr集群服务。solr停用也可执行导出，solr在用不影响线上服务。
2. 文件读取，速度快。
3. 全量数据导出，包括stored fields和docvalues。

其他导出方式存在的问题：
1. solr分页查询，深度分页无法完成查询。
2. solr cursorMark游标查询，需要排序字段有唯一值，且有排序开销。
3. solr /export RequestHandler，仅能导出docValue=true的字段。


目前只写了个demo自己导出用，hardcode了字段名，有需要的自行参考写法，欢迎交流。
导出参考数据：索引42亿文档TB级别，单分片4.2亿文档100GB导出耗时4小时，可多分片并行。

lucene版本：6.6.2，不同版本使用的类方法会有差异。

暂作记录，待有空再补充这块。
