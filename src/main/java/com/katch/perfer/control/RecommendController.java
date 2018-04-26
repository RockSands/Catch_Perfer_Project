package com.katch.perfer.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.katch.perfer.service.ConsumerNorthService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "recommend")
public class RecommendController {
	@Autowired
	private ConsumerNorthService consumerNorthService;

	/**
	 * 查询
	 * 
	 * @ApiOperation 是Swagger2的说明标签
	 */
	@ApiOperation(value = "获取推荐列表", notes = "")
	@RequestMapping(method = RequestMethod.GET)
	public List<Long> get(@RequestParam(required = false) Long yhid, @RequestParam(required = true) String qy) {
		if (yhid == null) {
			yhid = Long.MIN_VALUE;
		}
		return consumerNorthService.queryRecommends(yhid, qy);
	}

}