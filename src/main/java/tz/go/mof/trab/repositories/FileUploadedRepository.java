package tz.go.mof.trab.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import tz.go.mof.trab.models.UploadedFile;

public interface FileUploadedRepository extends PagingAndSortingRepository<UploadedFile,String> {

    Page<UploadedFile> findAll(Pageable pageable);
}
