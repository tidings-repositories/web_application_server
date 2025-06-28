package com.delivalue.tidings.domain.data.service;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${AWS_S3_MEDIA_BUCKET_NAME}")
    private String bucket;
    private final MemberRepository memberRepository;

    public URL getProfilePresignedUploadUrl(String internalId, String contentType) {
        Optional<Member> member = this.memberRepository.findById(internalId);
        if(member.isEmpty()) return null;

        String publicId = member.get().getPublicId();
        String uuid = UUID.randomUUID().toString();
        String path = "profile/" + publicId + "/" + uuid;

        return this.generatePresignedUploadUrl(path, contentType);
    }

    private URL generatePresignedUploadUrl(String path, String contentType) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucket)
                .key(path)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = presigner.presignPutObject(presignRequest);
        return presignedPutObjectRequest.url();
    }
}
