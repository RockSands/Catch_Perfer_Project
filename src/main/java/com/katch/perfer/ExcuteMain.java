package com.katch.perfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.katch.perfer.kettle.metas.KettleTableMeta;
import com.katch.perfer.kettle.service.KettleNorthService;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.katch.perfer.mybatis.mapper")
public class ExcuteMain {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ExcuteMain.class, args);
//		TestService TestService = context.getBean(TestService.class);
		KettleNorthService kettleNorthService = context.getBean(KettleNorthService.class);
		System.out.println("------------------------------");
//		List<String> flags = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
//				"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
		 List<String> flags = Arrays.asList("A");
		/*
		 * Source的TableName无效
		 */
		List<KettleTableMeta> sources = new ArrayList<KettleTableMeta>(flags.size());
		List<KettleTableMeta> targets = new ArrayList<KettleTableMeta>(flags.size());
		KettleTableMeta source = null;
		KettleTableMeta target = null;
		for (String flag : flags) {
			// 源配置
			source = new KettleTableMeta();
			source.setType("MySQL");
			source.setHost("192.168.80.138");
			source.setPort("3306");
			source.setDatabase("employees");
			source.setUser("root");
			source.setPasswd("123456");
			if ("".equals(flag)) {
				source.setSql(
						"SELECT employees.emp_no, dept_emp.dept_no, employees.first_name, employees.last_name, employees.birth_date "
								+ "FROM employees, dept_emp WHERE employees.emp_no = dept_emp.emp_no");
			} else {
				source.setSql(
						"SELECT employees.emp_no, dept_emp.dept_no, employees.first_name, employees.last_name, employees.birth_date "
								+ "FROM employees, dept_emp WHERE employees.emp_no = dept_emp.emp_no AND first_name LIKE '"
								+ flag + "%'");
			}
			source.setColumns(Arrays.asList("emp_no", "dept_no", "first_name", "last_name", "birth_date"));
			source.setPkcolumns(Arrays.asList("emp_no", "dept_no"));
			sources.add(source);
			// 目标配置
			target = new KettleTableMeta();
			target.setType("MySQL");
			target.setHost("192.168.80.138");
			target.setPort("3306");
			target.setDatabase("person");
			target.setUser("root");
			target.setPasswd("123456");
			target.setColumns(Arrays.asList("empID", "deptID", "firstName", "lastName", "born"));
			target.setPkcolumns(Arrays.asList("empID", "deptID"));
			if ("".equals(flag)) {
				target.setSql("SELECT empID, deptID, firstName, lastName, born FROM target_employees");
				target.setTableName("target_employees");
			} else {
				target.setSql("SELECT empID, deptID, firstName, lastName, born FROM target_employees_" + flag);
				target.setTableName("target_employees_" + flag);
			}
			targets.add(target);
		}
		List<CreateSTDThread> createDataTransfers = new ArrayList<CreateSTDThread>(flags.size());
		ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(flags.size());
		for (int i = 0; i < flags.size(); i++) {
			CreateSTDThread cdt = new CreateSTDThread(kettleNorthService,sources.get(i), targets.get(i), null);
			threadPool.scheduleWithFixedDelay(cdt, 2, 1, TimeUnit.SECONDS);
			createDataTransfers.add(cdt);
		}
	}
}
