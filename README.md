# solr-data-export

读取lucene索引文件，solr全量数据导出。

优点：
1. 无需访问solr集群服务。solr停用也可执行导出，solr在用不影响线上服务。
2. 文件读取，速度快。
3. 全量数据导出，包括stored fields和docvalues。

其他导出方式存在的问题：
1. solr分页，深度分页无法完成查询。
2. solr cursorMark游标查询，需要排序字段有唯一值，且有排序开销。
3. solr /export RequestHandler，仅能导出docValue=true的字段。
