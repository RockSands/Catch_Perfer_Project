package com.katch.perfer.mybatis.batch;

import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MybatisBatchRepoRepository {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	/**
	 * 批量执行
	 * 
	 * @param vos
	 */
	public void excuteBatch(List<BatchVO> vos) {
		SqlSession session = null;
		try {
			session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
			for (Iterator<BatchVO> it = vos.iterator(); it.hasNext();) {
				BatchVO batchVO = it.next();
				String operate = batchVO.getOperate();
				if (BatchVO.OPERATION_INSERT.equals(operate)) {
					session.insert(batchVO.getString(), batchVO.getObject());
				} else if (BatchVO.OPERATION_DELET.equals(operate)) {
					session.delete(batchVO.getString(), batchVO.getObject());
				} else if (BatchVO.OPERATION_UPDATE.equals(operate)) {
					session.update(batchVO.getString(), batchVO.getObject());
				} else {
					throw new UnsupportedOperationException("Invalidate operator '" + operate + "' in batch VO");
				}
			}
			session.commit();
			session.clearCache();
		} catch (Exception ex) {
			session.rollback();
			throw ex;
		} finally {
			session.close();
		}
	}
}
