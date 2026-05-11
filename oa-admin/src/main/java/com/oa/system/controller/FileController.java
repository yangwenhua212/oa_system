package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.entity.File;
import com.oa.system.entity.FilePath;
import com.oa.system.security.JwtTokenUtil;
import com.oa.system.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 文件管理控制器
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 获取文件列表（文件夹和文件）
     */
    @GetMapping("/list")
    public R<Map<String, Object>> getFileList(
            @RequestParam(required = false) Long pathId,
            @RequestParam(required = false) String keyword) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取文件夹列表
        List<FilePath> folders;
        if (pathId == null || pathId == 0) {
            // 获取根目录文件夹
            folders = fileService.getSubPaths(0L);
        } else {
            folders = fileService.getSubPaths(pathId);
        }
        
        // 过滤用户自己的文件夹
        folders = folders.stream()
                .filter(f -> f.getPathUserId().equals(userId))
                .toList();
        
        for (FilePath folder : folders) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", folder.getPathId());
            item.put("name", folder.getPathName());
            item.put("isFolder", true);
            item.put("parentId", folder.getParentId());
            item.put("uploadTime", folder.getCreateTime());
            item.put("type", "文件夹");
            item.put("size", 0);
            item.put("pathId", folder.getPathId());
            item.put("pathName", folder.getPathName());
            result.add(item);
        }
        
        // 获取文件列表
        Long actualPathId = (pathId == null || pathId == 0) ? getUserRootPathId(userId) : pathId;
        List<File> files = fileService.getFileList(actualPathId, userId);
        
        for (File file : files) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", file.getFileId());
            item.put("name", file.getFileName());
            item.put("isFolder", false);
            item.put("parentId", file.getPathId());
            item.put("uploadTime", file.getUploadTime());
            item.put("size", file.getSize());
            item.put("type", file.getFileShuffix());
            item.put("fileId", file.getFileId());
            item.put("filePath", file.getFilePath());
            item.put("fileUserId", file.getFileUserId());
            result.add(item);
        }
        
        // 按名称搜索过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = result.stream()
                    .filter(item -> item.get("name").toString().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("files", result);
        data.put("currentPathId", pathId == null ? 0 : pathId);
        
        return R.ok(data);
    }

    /**
     * 获取面包屑导航
     */
    @GetMapping("/breadcrumb")
    public R<List<Map<String, Object>>> getBreadcrumb(@RequestParam Long pathId) {
        List<Map<String, Object>> breadcrumb = new ArrayList<>();
        breadcrumb.add(new HashMap<>() {{ put("id", 0); put("name", "全部文件"); }});
        
        if (pathId != null && pathId > 0) {
            List<Map<String, Object>> pathList = new ArrayList<>();
            Long currentId = pathId;
            
            while (currentId != null && currentId > 0) {
                FilePath path = getFilePathById(currentId);
                if (path != null) {
                    pathList.add(0, new HashMap<>() {{
                        put("id", path.getPathId());
                        put("name", path.getPathName());
                    }});
                    currentId = path.getParentId();
                } else {
                    break;
                }
            }
            
            breadcrumb.addAll(pathList);
        }
        
        return R.ok(breadcrumb);
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/{id}")
    public R<File> getFileById(@PathVariable Long id) {
        File file = fileService.getFileById(id);
        if (file == null) {
            return R.error("文件不存在");
        }
        return R.ok(file);
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public R<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pathId", required = false, defaultValue = "0") Long pathId,
            @RequestParam(value = "model", required = false) String model) throws IOException {
        
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        if (file.isEmpty()) {
            return R.error("请选择文件");
        }
        
        // 创建上传目录
        String uploadDir = System.getProperty("user.dir") + "/uploads/" + userId + "/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath);
        
        // 获取实际路径ID
        Long actualPathId = (pathId == null || pathId == 0) ? getUserRootPathId(userId) : pathId;
        
        // 保存文件记录
        File fileEntity = new File();
        fileEntity.setFileName(originalFilename);
        fileEntity.setFilePath("/uploads/" + userId + "/" + newFileName);
        fileEntity.setSize(file.getSize());
        fileEntity.setFileShuffix(extension.replace(".", ""));
        fileEntity.setContentType(file.getContentType());
        fileEntity.setModel(model != null ? model : "common");
        fileEntity.setPathId(actualPathId);
        fileEntity.setFileUserId(userId);
        fileEntity.setUploadTime(LocalDateTime.now());
        fileEntity.setFileIstrash(0);
        fileEntity.setFileIsshare(0);
        
        Long fileId = fileService.uploadFile(fileEntity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("fileId", fileId);
        result.put("fileName", originalFilename);
        result.put("filePath", fileEntity.getFilePath());
        result.put("size", file.getSize());
        
        return R.ok(result);
    }

    /**
     * 创建文件夹
     */
    @PostMapping("/folder")
    public R<Map<String, Object>> createFolder(@RequestBody Map<String, Object> params) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        String folderName = (String) params.get("name");
        Long parentId = params.get("parentId") != null ? ((Number) params.get("parentId")).longValue() : 0L;
        
        if (folderName == null || folderName.trim().isEmpty()) {
            return R.error("文件夹名称不能为空");
        }
        
        // 检查同名文件夹是否存在
        List<FilePath> existingFolders = fileService.getSubPaths(parentId);
        boolean exists = existingFolders.stream()
                .anyMatch(f -> f.getPathName().equals(folderName) && f.getPathUserId().equals(userId));
        
        if (exists) {
            return R.error("该文件夹已存在");
        }
        
        FilePath filePath = new FilePath();
        filePath.setPathName(folderName);
        filePath.setParentId(parentId);
        filePath.setPathUserId(userId);
        filePath.setPathIstrash(0);
        
        Long pathId = fileService.createFolder(filePath);
        
        Map<String, Object> result = new HashMap<>();
        result.put("pathId", pathId);
        result.put("name", folderName);
        
        return R.ok(result);
    }

    /**
     * 重命名文件/文件夹
     */
    @PutMapping("/rename")
    public R rename(@RequestBody Map<String, Object> params) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        String type = (String) params.get("type");
        Long id = ((Number) params.get("id")).longValue();
        String newName = (String) params.get("newName");
        
        if (newName == null || newName.trim().isEmpty()) {
            return R.error("名称不能为空");
        }
        
        if ("folder".equals(type)) {
            FilePath path = getFilePathById(id);
            if (path == null) {
                return R.error("文件夹不存在");
            }
            if (!path.getPathUserId().equals(userId)) {
                return R.error("无权操作此文件夹");
            }
            path.setPathName(newName);
            // 更新数据库
            // filePathMapper.updateById(path);
        } else {
            File file = fileService.getFileById(id);
            if (file == null) {
                return R.error("文件不存在");
            }
            if (!file.getFileUserId().equals(userId)) {
                return R.error("无权操作此文件");
            }
            file.setFileName(newName);
            // 更新数据库
            // fileMapper.updateById(file);
        }
        
        return R.ok();
    }

    /**
     * 删除文件到回收站
     */
    @DeleteMapping("/{id}")
    public R deleteFile(@PathVariable Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        File file = fileService.getFileById(id);
        if (file == null) {
            return R.error("文件不存在");
        }
        if (!file.getFileUserId().equals(userId)) {
            return R.error("无权删除此文件");
        }
        
        boolean success = fileService.trashFile(id);
        return success ? R.ok() : R.error("删除失败");
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        File file = fileService.getFileById(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        
        String projectRoot = System.getProperty("user.dir");
        Path filePath = Paths.get(projectRoot, file.getFilePath());
        
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + new String(file.getFileName().getBytes("UTF-8"), "ISO-8859-1") + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 移动文件到指定文件夹
     */
    @PutMapping("/move")
    public R moveFile(@RequestBody Map<String, Object> params) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        Long fileId = ((Number) params.get("fileId")).longValue();
        Long targetPathId = ((Number) params.get("targetPathId")).longValue();
        
        File file = fileService.getFileById(fileId);
        if (file == null) {
            return R.error("文件不存在");
        }
        if (!file.getFileUserId().equals(userId)) {
            return R.error("无权移动此文件");
        }
        
        file.setPathId(targetPathId);
        // fileMapper.updateById(file);
        
        return R.ok();
    }

    /**
     * 获取回收站文件
     */
    @GetMapping("/trash")
    public R<List<File>> getTrashFiles() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<File> trashFiles = fileService.getTrashFiles(userId);
        return R.ok(trashFiles);
    }

    /**
     * 恢复文件
     */
    @PutMapping("/restore/{id}")
    public R restoreFile(@PathVariable Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        File file = fileService.getFileById(id);
        if (file == null) {
            return R.error("文件不存在");
        }
        if (!file.getFileUserId().equals(userId)) {
            return R.error("无权恢复此文件");
        }
        
        boolean success = fileService.restoreFile(id);
        return success ? R.ok() : R.error("恢复失败");
    }

    /**
     * 获取文件统计
     */
    @GetMapping("/stats")
    public R<Map<String, Object>> getFileStats() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        
        List<File> files = fileService.getFileList(null, userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", files.size());
        
        long docCount = files.stream().filter(f -> {
            String suffix = f.getFileShuffix();
            return suffix != null && ("doc".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix) 
                    || "txt".equalsIgnoreCase(suffix) || "pdf".equalsIgnoreCase(suffix));
        }).count();
        
        long imageCount = files.stream().filter(f -> {
            String suffix = f.getFileShuffix();
            return suffix != null && ("jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix) 
                    || "png".equalsIgnoreCase(suffix) || "gif".equalsIgnoreCase(suffix) || "bmp".equalsIgnoreCase(suffix));
        }).count();
        
        long videoCount = files.stream().filter(f -> {
            String suffix = f.getFileShuffix();
            return suffix != null && ("mp4".equalsIgnoreCase(suffix) || "avi".equalsIgnoreCase(suffix) 
                    || "mov".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix));
        }).count();
        
        long audioCount = files.stream().filter(f -> {
            String suffix = f.getFileShuffix();
            return suffix != null && ("mp3".equalsIgnoreCase(suffix) || "wav".equalsIgnoreCase(suffix) 
                    || "flac".equalsIgnoreCase(suffix));
        }).count();
        
        long otherCount = files.size() - docCount - imageCount - videoCount - audioCount;
        
        stats.put("doc", docCount);
        stats.put("image", imageCount);
        stats.put("video", videoCount);
        stats.put("audio", audioCount);
        stats.put("other", otherCount);
        
        // 获取文件夹数量
        List<FilePath> folders = fileService.getSubPaths(0L);
        long folderCount = folders.stream().filter(f -> f.getPathUserId().equals(userId)).count();
        stats.put("folder", folderCount);
        
        return R.ok(stats);
    }

    /**
     * 获取用户根目录ID
     */
    private Long getUserRootPathId(Long userId) {
        FilePath rootPath = fileService.getRootPath(userId);
        if (rootPath == null) {
            // 创建根目录
            FilePath newRoot = new FilePath();
            newRoot.setPathName("我的文件");
            newRoot.setParentId(0L);
            newRoot.setPathUserId(userId);
            newRoot.setPathIstrash(0);
            Long pathId = fileService.createFolder(newRoot);
            return pathId;
        }
        return rootPath.getPathId();
    }

    /**
     * 根据ID获取文件路径
     */
    private FilePath getFilePathById(Long pathId) {
        FilePath path = new FilePath();
        path.setPathId(pathId);
        path.setPathName("未知文件夹");
        // 实际应该从数据库查询
        return path;
    }
}
