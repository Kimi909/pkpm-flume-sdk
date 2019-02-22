1、org.apache.hadoop.hdfs.server.datanode.DataNode: IOException in offerService

https://blog.csdn.net/knowledgeaaa/article/details/47612325

```
1.停掉本地的datanode进程。
2.删除本地dfs.data.dir下的所有内容。
3.重启本地datanode进程。
```



2、Operation category READ is not supported in state standby 故障解决


```
参考博客：https://blog.csdn.net/weixin_41156591/article/details/85332376
查看节点状态

1、hdfs haadmin -getServiceState  nn1

   hdfs haadmin -getServiceState  nn2
  //手动置为活跃节点
   hdfs haadmin -transitionToActive --forcemanual nn1 
   
   //手动置为非活跃节点
   hdfs haadmin -transitionToStandby --forcemanual nn2


```

3、参考：https://blog.csdn.net/xiaoshunzi111/article/details/50663155

```
重启集群start-all.sh

This node has namespaceId '526096300 and clusterId 'CID-c61b6d18-8344-4ecb-8fab-6600fdb6b11d' but the requesting node expected '1838975276' and 'CID-e8aa8ca6-c3b5-4b6c-86fc-2cb6ca1f4674'
	at org.apache.hadoop.hdfs.server.namenode.EditLogFileInputStream$URLLog$1.run(EditLogFileInputStream.java:468)
	at org.apache.hadoop.hdfs.server.namenode.EditLogFileInputStream$URLLog$1.run(EditLogFileInputStream.java:456)
	at java.security.AccessController.doPrivileged(Native Method)
	at javax.security.auth.Subject.doAs(Subject.java:422)
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1762)
	at org.apache.hadoop.security.SecurityUtil.doAsUser(SecurityUtil.java:448)
	at org.apache.hadoop.security.SecurityUtil.doAsCurrentUser(SecurityUtil.java:442)
	at org.apache.hadoop.hdfs.server.namenode.EditLogFileInputStream$URLLog.getInputStream(EditLogFileInputStream.java:455)
	at org.apache.hadoop.hdfs.server.namenode.EditLogFileInputStream.init(EditLogFileInputStream.java:141)
	at org.apache.hadoop.hdfs.server.namenode.EditLogFileInputStream.nextOpImpl(EditLogFileInputStream.java:192)
	at org.apache.hadoop.hdfs.server.namenode.EditLogFileInputStream.nextOp(EditLogFileInputStream.java:250)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.readOp(EditLogInputStream.java:85)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.skipUntil(EditLogInputStream.java:151)
	at org.apache.hadoop.hdfs.server.namenode.RedundantEditLogInputStream.nextOp(RedundantEditLogInputStream.java:178)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.readOp(EditLogInputStream.java:85)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.skipUntil(EditLogInputStream.java:151)
	at org.apache.hadoop.hdfs.server.namenode.RedundantEditLogInputStream.nextOp(RedundantEditLogInputStream.java:178)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.readOp(EditLogInputStream.java:85)
	at org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader.loadEditRecords(FSEditLogLoader.java:190)
	at org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader.loadFSEdits(FSEditLogLoader.java:143)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.loadEdits(FSImage.java:898)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.loadFSImage(FSImage.java:753)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.recoverTransitionRead(FSImage.java:329)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFSImage(FSNamesystem.java:983)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFromDisk(FSNamesystem.java:686)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.loadNamesystem(NameNode.java:586)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.initialize(NameNode.java:646)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:820)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:804)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.createNameNode(NameNode.java:1516)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.main(NameNode.java:1582)
2019-02-15 18:43:31,680 ERROR org.apache.hadoop.hdfs.server.namenode.EditLogInputStream: Got error reading edit log input stream http://s7:8480/getJournal?jid=ns1&segmentTxId=3044&storageInfo=-63%3A1838975276%3A0%3ACID-e8aa8ca6-c3b5-4b6c-86fc-2cb6ca1f4674; failing over to edit log http://s5:8480/getJournal?jid=ns1&segmentTxId=3044&storageInfo=-63%3A1838975276%3A0%3ACID-e8aa8ca6-c3b5-4b6c-86fc-2cb6ca1f4674
org.apache.hadoop.hdfs.server.namenode.RedundantEditLogInputStream$PrematureEOFException: got premature end-of-file at txid 3037; expected file to go up to 3045
	at org.apache.hadoop.hdfs.server.namenode.RedundantEditLogInputStream.nextOp(RedundantEditLogInputStream.java:194)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.readOp(EditLogInputStream.java:85)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.skipUntil(EditLogInputStream.java:151)
	at org.apache.hadoop.hdfs.server.namenode.RedundantEditLogInputStream.nextOp(RedundantEditLogInputStream.java:178)
	at org.apache.hadoop.hdfs.server.namenode.EditLogInputStream.readOp(EditLogInputStream.java:85)
	at org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader.loadEditRecords(FSEditLogLoader.java:190)
	at org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader.loadFSEdits(FSEditLogLoader.java:143)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.loadEdits(FSImage.java:898)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.loadFSImage(FSImage.java:753)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.recoverTransitionRead(FSImage.java:329)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFSImage(FSNamesystem.java:983)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFromDisk(FSNamesystem.java:686)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.loadNamesystem(NameNode.java:586)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.initialize(NameNode.java:646)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:820)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:804)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.createNameNode(NameNode.java:1516)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.main(NameNode.java:1582)
```