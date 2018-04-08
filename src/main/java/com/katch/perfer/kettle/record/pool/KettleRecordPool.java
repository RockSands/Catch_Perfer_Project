package com.katch.perfer.kettle.record.pool;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.pentaho.di.core.exception.KettleException;

import com.katch.perfer.consist.kettle.KettleVariables;
import com.katch.perfer.domain.kettle.KettleRecord;

/**
 * Kettle任务池
 * 
 * @author chenkw
 *
 */
public class KettleRecordPool {

	/**
	 * 存储Record的Map
	 */
	private Map<String, KettleRecord> recordCache = new WeakHashMap<String, KettleRecord>();

	/**
	 * 记录队列
	 */
	private final Queue<String> recordQueue = new LinkedBlockingQueue<String>();

	/**
	 * 优先记录队列
	 */
	private final Queue<String> recordPrioritizeQueue = new LinkedBlockingQueue<String>();

	/**
	 * 监听者
	 */
	private List<KettleRecordPoolMonitor> poolMonitors = new LinkedList<KettleRecordPoolMonitor>();

	/**
	 * @throws Exception
	 */
	public KettleRecordPool() throws Exception {
	}

	/**
	 * 注册监听器
	 * 
	 * @param poolMonitor
	 */
	public void registePoolMonitor(KettleRecordPoolMonitor poolMonitor) {
		poolMonitors.add(poolMonitor);
	}

	/**
	 * 注册监听器
	 * 
	 * @param poolMonitor
	 */
	public void registePoolMonitor(Collection<KettleRecordPoolMonitor> poolMonitors) {
		poolMonitors.addAll(poolMonitors);
	}

	/**
	 * 发送通知
	 * 
	 * @param record
	 * 
	 */
	private void notifyPoolMonitors() {
		for (KettleRecordPoolMonitor poolMonitor : poolMonitors) {
			poolMonitor.addRecordNotify();
		}
	}

	/**
	 * 添加的转换任务,该任务仅执行一次
	 * 
	 * @param record
	 * @return 是否添加成功
	 * @throws KettleException
	 */
	public synchronized boolean addRecord(KettleRecord record) throws KettleException {
		if (record != null && !recordCache.containsKey(record.getUuid())) {
			check();
			recordCache.put(record.getUuid(), record);
			if (recordQueue.offer(record.getUuid())) {
				notifyPoolMonitors();
				return true;
			} else {
				recordCache.remove(record.getUuid());
				return false;
			}
		}
		return false;
	}

	/**
	 * 添加的转换任务-优先
	 * 
	 * @param record
	 * @return 是否添加成功
	 */
	public synchronized boolean addPrioritizeRecord(KettleRecord record) {
		if (record != null && !recordCache.containsKey(record.getUuid())) {
			recordCache.put(record.getUuid(), record);
			boolean result = recordPrioritizeQueue.offer(record.getUuid());
			if (result) {
				notifyPoolMonitors();
			}
			return result;
		}
		return false;
	}

	/**
	 * 删除
	 * 
	 * @param uuid
	 * @throws KettleException
	 */
	public synchronized boolean deleteRecord(String uuid) {
		recordPrioritizeQueue.remove(uuid);
		recordQueue.remove(uuid);
		recordCache.remove(uuid);
		return true;
	}

	/**
	 * 获取下一个,并在Pool中删除
	 * 
	 * @return
	 */
	public synchronized KettleRecord nextRecord() {
		KettleRecord record = null;
		String recordUUID = null;
		if (!recordPrioritizeQueue.isEmpty()) {
			recordUUID = recordPrioritizeQueue.poll();
		}
		if (recordUUID == null && !recordQueue.isEmpty()) {
			recordUUID = recordQueue.poll();
		}
		if (recordUUID != null) {
			record = recordCache.remove(recordUUID);
		}
		return record;
	}

	/**
	 * 任务数量
	 * 
	 * @return
	 */
	public int size() {
		return recordQueue.size() + recordPrioritizeQueue.size();
	}

	/**
	 * 任务数量验证
	 * 
	 * @return
	 * @throws KettleException
	 */
	private void check() throws KettleException {
		if (KettleVariables.KETTLE_RECORD_POOL_MAX != null
				&& size() > KettleVariables.KETTLE_RECORD_POOL_MAX) {
			throw new KettleException("KettleRecordPool的任务数量已满,无法接受任务!");
		}
	}
}
