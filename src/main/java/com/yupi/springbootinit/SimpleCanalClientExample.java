package com.yupi.springbootinit;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;


import cn.hutool.core.date.DateTime;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import net.sf.jsqlparser.statement.select.Wait;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class SimpleCanalClientExample {

    @Resource
    PostEsDao postEsDao;

    @PostConstruct
    public void run() {
        new Thread(()->{
            // 创建链接
            CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("8.134.166.133",
                    11111), "example", "", "");
            int batchSize = 1000;
            try {
                connector.connect();
                connector.subscribe(".*\\..*");
                connector.rollback();
                while (true) {

                    Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
//                        System.out.println("empty count : " + emptyCount);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } else {
                        // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                        printEntry(message.getEntries());
                    }

                    connector.ack(batchId); // 提交确认
                    // connector.rollback(batchId); // 处理失败, 回滚数据
                }

//                System.out.println("empty too many times, exit");
            } finally {
                connector.disconnect();
            }
        }).start();

    }

    private void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------&gt; after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private void printColumn(List<Column> columns) {
        PostEsDTO postEsDTO = new PostEsDTO();
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());

            switch (column.getName()) {
                case "id":
                    postEsDTO.setId(Long.valueOf(column.getValue()));
                    break;
                case "title":
                    postEsDTO.setTitle(column.getValue());
                    break;
                case "content":
                    postEsDTO.setContent(column.getValue());
                    break;
                case "tags":
                    postEsDTO.setTags(Collections.singletonList(column.getValue()));
                    break;
                case "userId":
                    postEsDTO.setUserId(Long.valueOf(column.getValue()));
                    break;
                case "createTime":
                    postEsDTO.setCreateTime(new DateTime(column.getValue()));
                    break;
                case "updateTime":
                    postEsDTO.setUpdateTime(new DateTime(column.getValue()));
                    break;
                case "isDelete":
                    String value = column.getValue();
                    if (StringUtils.isNotBlank(value)) {
                        postEsDTO.setIsDelete(Integer.valueOf(value));
                    }
                    break;
                default:
                    // do nothing
            }

            if (StringUtils.isNotBlank(postEsDTO.getTitle())) {
                postEsDao.save(postEsDTO);
            }
        }
    }

}