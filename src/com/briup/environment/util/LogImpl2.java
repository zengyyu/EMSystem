package com.briup.environment.util;

import java.util.Properties;

import org.apache.log4j.Logger;

/*
 * 日志模块
 */
public class LogImpl2 implements Log {

	//创建日志对象
	private Logger logger = Logger.getLogger(LogImpl2.class);
	
	@Override
	public void init(Properties properties) throws Exception {
		
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void fatal(String message) {
		logger.fatal(message);
	}
	
	/*public static void main(String[] args) {
		System.out.println("hello world");
		new LogImpl2().info("hello everyone");
	}*/
}
