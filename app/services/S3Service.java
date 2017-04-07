package services;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.RandomStringUtils;
import play.api.Play;

import utility.SimpleImageInfo;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

public class S3Service {
	public static final String S3BUCKETNAME = "images.tolet.com.ng";
	public static final String TMPMAINDIR = "/home/toletdeveloper2/images/";
	public static final String S3LOGODIR = "logodir/";
	
	public static AmazonS3Client s3Client;
	static {
		try {
			s3Client = new AmazonS3Client(new PropertiesCredentials(Play.classloader(Play.current()).getResourceAsStream("aws.properties")));
		} catch (Exception e) { e.printStackTrace(); }

		try {
			createDirectory(TMPMAINDIR);
		} catch (Exception e) { e.printStackTrace(); }

	}

	private static void createDirectory(String directory) {
		File file = new File(directory);
		if (!file.exists())
			file.mkdir();
	}

	public static String storeLogo(File original, String fileName) throws Exception {

		File main = new File(TMPMAINDIR + fileName);

        SimpleImageInfo info = new SimpleImageInfo(original);
        int width = info.getWidth();
        if(width > 400) {
            Thumbnails.of(original)
                .width(400)
                .toFile(main);
        } else {
            original.renameTo(main);
        }

        uploadToS3(S3LOGODIR + fileName, main);

		return fileName;
	}

	private static void uploadToS3(String key, File file) {
		PutObjectRequest p = new PutObjectRequest(S3BUCKETNAME, key, file);
		p.setCannedAcl(CannedAccessControlList.PublicRead);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setLastModified(new Date());
        p.setMetadata(meta);

		s3Client.putObject(p);
	}

	private static void uploadToS3(String key, InputStream input, String contentType, Long size) {
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(contentType);
		meta.setContentLength(size);
		PutObjectRequest p = new PutObjectRequest(S3BUCKETNAME, key, input, meta);
		p.setCannedAcl(CannedAccessControlList.PublicRead);
		s3Client.putObject(p);
	}

	private static void deleteFromS3(String key) {
		s3Client.deleteObject(S3BUCKETNAME, key);
	}

	public static String getFileExtension(String fileName) {
		String ext = "";
		int mid = fileName.lastIndexOf(".");

		ext = fileName.substring(mid + 1, fileName.length());
		if("pdf".equalsIgnoreCase(ext)){
			return "png";
		}
		return ext.toLowerCase();
	}

    public static String generateUniqueId(int length) {
	    return RandomStringUtils.randomAlphanumeric(length);
	}

}
