package com.katch.perfer.mahout.service;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.ConsumerExportCSVProperties;

public abstract class MahoutRecommenderService {
    private static Logger logger = LoggerFactory.getLogger(UserMahoutRecommenderService.class);

    private DecimalFormat df = new DecimalFormat("##0.###");

    @Autowired
    protected ConsumerExportCSVProperties consumerExportCSVProperties;

    /**
     * 执行
     * 
     * @throws Exception
     */
    public abstract void excute() throws Exception;

    /**
     * 
     * @param dataModel
     * @throws Exception
     */
    protected void saveUserRecommender(DataModel dataModel, Recommender recommender) throws Exception {
	logger.debug("消费推荐导出文件准备导出!");
	LongPrimitiveIterator it = dataModel.getUserIDs();
	Long userID = null;
	Path path = Paths.get(consumerExportCSVProperties.getOutputFileName());
	BufferedWriter writer = Files.newBufferedWriter(path);
	while (it.hasNext()) {
	    userID = it.next();
	    if (userID == null) {
		continue;
	    }
	    for (RecommendedItem recommendedItem : recommender.recommend(userID, 50)) {
		if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
		    continue;
		}
		writer.write(userID + "," + recommendedItem.getItemID() + "," + df.format(recommendedItem.getValue()));
		writer.newLine();
	    }
	}
	writer.close();
	
    }

    public ConsumerExportCSVProperties getConsumerExportCSVProperties() {
	return consumerExportCSVProperties;
    }

    public void setConsumerExportCSVProperties(ConsumerExportCSVProperties consumerExportCSVProperties) {
	this.consumerExportCSVProperties = consumerExportCSVProperties;
    }
}
