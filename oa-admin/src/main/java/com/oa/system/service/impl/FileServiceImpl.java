package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.entity.File;
import com.oa.system.entity.FilePath;
import com.oa.system.mapper.FileMapper;
import com.oa.system.mapper.FilePathMapper;
import com.oa.system.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件服务实现
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final FilePathMapper filePathMapper;

    @Override
    public List<File> getFileList(Long pathId, Long userId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getFileUserId, userId)
               .eq(File::getFileIstrash, 0);
        
        if (pathId != null && pathId > 0) {
            wrapper.eq(File::getPathId, pathId);
        }
        
        wrapper.orderByDesc(File::getUploadTime);
        return fileMapper.selectList(wrapper);
    }

    @Override
    public FilePath getRootPath(Long userId) {
        LambdaQueryWrapper<FilePath> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FilePath::getPathUserId, userId)
               .eq(FilePath::getParentId, 0)
               .eq(FilePath::getPathIstrash, 0)
               .last("LIMIT 1");
        return filePathMapper.selectOne(wrapper);
    }

    @Override
    public List<FilePath> getSubPaths(Long parentId) {
        LambdaQueryWrapper<FilePath> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FilePath::getParentId, parentId)
               .eq(FilePath::getPathIstrash, 0)
               .orderByAsc(FilePath::getPathId);
        return filePathMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Long createFolder(FilePath filePath) {
        filePathMapper.insert(filePath);
        return filePath.getPathId();
    }

    @Override
    @Transactional
    public boolean trashFile(Long fileId) {
        File file = new File();
        file.setFileId(fileId);
        file.setFileIstrash(1);
        return fileMapper.updateById(file) > 0;
    }

    @Override
    @Transactional
    public boolean deleteFile(Long fileId) {
        return fileMapper.deleteById(fileId) > 0;
    }

    @Override
    @Transactional
    public boolean restoreFile(Long fileId) {
        File file = new File();
        file.setFileId(fileId);
        file.setFileIstrash(0);
        return fileMapper.updateById(file) > 0;
    }

    @Override
    public List<File> getTrashFiles(Long userId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getFileUserId, userId)
               .eq(File::getFileIstrash, 1)
               .orderByDesc(File::getUploadTime);
        return fileMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Long uploadFile(File file) {
        fileMapper.insert(file);
        return file.getFileId();
    }

    @Override
    public File getFileById(Long id) {
        return fileMapper.selectById(id);
    }

    @Override
    @Transactional
    public void updateFileById(File file) {
        fileMapper.updateById(file);
    }

    @Override
    @Transactional
    public void updateFilePathById(FilePath filePath) {
        filePathMapper.updateById(filePath);
    }
}
