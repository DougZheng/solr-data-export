import com.google.gson.JsonObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("args needed: ${index_file} ${output_file} [${start_line} ${read_num}]");
            System.exit(1);
        }
        final String index_file = args[0];
        final String output_file = args[1];
        int start_line = 0;
        int read_num = Integer.MAX_VALUE;
        if (args.length > 2) {
            start_line = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            read_num = Integer.parseInt(args[3]);
        }
        System.err.println("index file: " + index_file);
        System.err.println("output file: " + output_file);
        System.err.println("start line: " + start_line);
        System.err.println("read num: " + read_num);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(output_file), StandardCharsets.UTF_8);
        Directory dir = FSDirectory.open(Paths.get(index_file));
        IndexReader index_reader = DirectoryReader.open(dir);
        Bits live_docs = MultiFields.getLiveDocs(index_reader);
        System.err.println("max doc: " + index_reader.maxDoc());
        System.err.println("num doc: " + index_reader.numDocs());
        final String[] doc_value_fields = {"xxxxx", "yyyyy"};
        List<LeafReaderContext> contexts = index_reader.leaves();
        int cur_ctx_idx = 0;
        int cur_doc_base = 0;
        int nxt_doc_base = contexts.size() > 1 ? contexts.get(1).docBase : index_reader.maxDoc();
        while (start_line >= nxt_doc_base && cur_ctx_idx + 1 < contexts.size()) {
            ++cur_ctx_idx;
            cur_doc_base = nxt_doc_base;
            nxt_doc_base = cur_ctx_idx + 1 < contexts.size() ?
                    contexts.get(cur_ctx_idx + 1).docBase : index_reader.maxDoc();
        }
        int limit_i = Math.min(start_line + read_num, index_reader.maxDoc());
        int del_cnt = 0;
        int live_cnt = 0;
        for (int i = start_line; i < limit_i; ++i) {
            boolean is_del = false;
            if (live_docs != null && !live_docs.get(i)) {
                is_del = true;
                ++del_cnt;
            }
            if (!is_del) {
                Document doc = index_reader.document(i);
                LeafReader leaf_reader = contexts.get(cur_ctx_idx).reader();
                JsonObject json_obj = new JsonObject();
                // read docvalues
                NumericDocValues num_doc_values = null;
                for (String field : doc_value_fields) {
                    num_doc_values = DocValues.getNumeric(leaf_reader, field);
                    json_obj.addProperty(field, num_doc_values.get(i - cur_doc_base));
                }
                // read stored values
                json_obj.addProperty("zzzzz", doc.get("zzzzz"));
                
                writer.write(json_obj.toString() + '\n');
                ++live_cnt;
            }
            if (i + 1 >= nxt_doc_base) {
                ++cur_ctx_idx;
                cur_doc_base = nxt_doc_base;
                nxt_doc_base = cur_ctx_idx + 1 < contexts.size() ?
                        contexts.get(cur_ctx_idx + 1).docBase : index_reader.maxDoc();
            }
            if (i % 50000 == 0) {
                System.err.println(index_file + " ---- " + i +
                        ", live docs " + live_cnt + ", deleted docs " + del_cnt);
            }
        }
        index_reader.close();
        dir.close();
        writer.close();
        System.err.println(index_file + " ---- end" +
                ", live docs " + live_cnt + ", deleted docs " + del_cnt);
    }
}
