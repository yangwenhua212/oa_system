package com.oa.system.service;

import com.oa.system.entity.File;
import com.oa.system.entity.FilePath;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 获取用户文件列表
     */
    List<File> getFileList(Long pathId, Long userId);

    /**
     * 获取用户根目录
     */
    FilePath getRootPath(Long userId);

    /**
     * 获取子目录
     */
    List<FilePath> getSubPaths(Long parentId);

    /**
     * 创建文件夹
     */
    Long createFolder(FilePath filePath);

    /**
     * 删除文件到回收站
     */
    boolean trashFile(Long fileId);

    /**
     * 彻底删除文件
     */
    boolean deleteFile(Long fileId);

    /**
     * 恢复文件
     */
    boolean restoreFile(Long fileId);

    /**
     * 获取回收站文件
     */
    List<File> getTrashFiles(Long userId);

    /**
     * 上传文件记录
     */
    Long uploadFile(File file);

    /**
     * 根据ID查询文件
     */
    File getFileById(Long id);
}
