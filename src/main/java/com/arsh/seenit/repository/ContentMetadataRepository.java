package com.arsh.seenit.repository;

import com.arsh.seenit.model.ContentMetadata;
import org.springframework.data.repository.CrudRepository;

public interface ContentMetadataRepository
        extends CrudRepository<ContentMetadata, String> {

    ContentMetadata upsert(ContentMetadata metadata);
}