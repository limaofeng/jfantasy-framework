package com.fantasy.file;

/**
 * 文件匹配过滤器
 * 
 * @author 李茂峰
 * @since 2013-9-8 下午4:49:48
 * @version 1.0
 */
public interface FileItemFilter {

	boolean accept(FileItem item);

}