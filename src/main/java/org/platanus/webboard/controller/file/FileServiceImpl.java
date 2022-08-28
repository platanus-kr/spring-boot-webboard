package org.platanus.webboard.controller.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platanus.webboard.config.property.PropertyEnvironment;
import org.platanus.webboard.controller.file.dto.FileDownloadDto;
import org.platanus.webboard.controller.file.dto.FileStoreDto;
import org.platanus.webboard.controller.file.dto.FileUploadDto;
import org.platanus.webboard.controller.user.UserService;
import org.platanus.webboard.domain.File;
import org.platanus.webboard.domain.FileRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final StorageManagement storageManagement;
    private final PropertyEnvironment propertyEnvironment;
    private final UserService userService;

    @Override
    public List<File> uploadFiles(FileUploadDto attachDto) throws IOException {
        if (attachDto.getUserId() < 1) {
            return null;
        }
        try {
            userService.findById(attachDto.getUserId());
        } catch (Exception e) {
            return null;
        }
        List<File> uploadedFiles = new ArrayList<>();
        // 파일시스템에 파일 저장.
        List<FileStoreDto> attachFiles = storageManagement.storeFiles(attachDto.getFiles());
        // DB에 파일 저장 정보 기록.
        for (FileStoreDto attachFile : attachFiles) {
            File uploadedFile = File.builder()
                    .userId(attachDto.getUserId())
                    .originalFilename(attachFile.getOriginalFilename())
                    .originalExtension(attachFile.getOriginalExtension())
                    .managementFilename(attachFile.getManagementFilename())
                    .storePathPrefix(attachFile.getStorePathPrefix())
                    .size(attachFile.getSize())
                    .deleted(false)
                    .expireDate(LocalDateTime.parse(attachDto.getExpireDate()))
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();
            uploadedFiles.add(uploadedFile);
            fileRepository.upload(uploadedFile);
        }
        return uploadedFiles;
    }

    @Override
    public FileDownloadDto findById(Long fileId) {
        Optional<File> findFile = fileRepository.findById(fileId);
        if (findFile.isEmpty()) {
            return null;
        }
        File file = findFile.get();
        FileDownloadDto fileDto = FileDownloadDto.fromFile(file);
        String originalFilename = fileDto.getOriginalFilename();
        String managementFilename = fileDto.getManagementFilename();
        Path storagePath = Paths.get(propertyEnvironment.getAttachFileStoragePath(), fileDto.getStorePathPrefix());
        fileDto.setOriginalFilenameWithFullPath(storageManagement.getFileFullPath(storagePath, managementFilename));

        return fileDto;
    }

    @Override
    public String getStoreFullPathByFilename(String managementFilenameWithStorePathPrefix) {
        // DB 에서 읽어올 일이 아니다.. 리소스는 다이렉트로 꽂아주자.
        // Optional<File> findFile = fileRepository.findByManagementFilename(managementFilenameWithStorePathPrefix);
        // if (findFile.isEmpty()) {
        //     return null;
        // }
        return Paths.get(propertyEnvironment.getAttachFileStoragePath(),
                        managementFilenameWithStorePathPrefix)
                .toString();

    }
}
