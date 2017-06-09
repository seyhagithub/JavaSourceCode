package com.proxywebservice.controller.kubota;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by hiemseyha on 6/8/17.
 */

@RestController
@RequestMapping("kubota/upload")
public class KubotaUploadController {

    final private String BASE_URL = "upload_file/";
    final private String SINGLE_FILE = "single_file";
    final private String MULTI_FILE = "multiple_file";

    private final Logger logger = LoggerFactory.getLogger(KubotaUploadController.class);


    @Autowired
    private HttpHeaders header;

    @Autowired
    private RestTemplate rest;

    @Autowired
    private String BASE_URL_KUBOTA;

    @RequestMapping(value = "/single", method = RequestMethod.POST,  consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> singleUpload(@RequestParam("file") MultipartFile file) {

        HttpEntity<Object> request= new HttpEntity<Object>(file,header);
        ResponseEntity<Map> response = rest.exchange(BASE_URL_KUBOTA + this.BASE_URL + this.SINGLE_FILE, HttpMethod.POST,request, Map.class);
        return new ResponseEntity<Map<String, Object>>(response.getBody(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = {"application/json"},headers=("content-type=multipart/*"))
    public ResponseEntity<Map<String,Object>> addImage(@RequestParam("file") CommonsMultipartFile file){
        System.out.println("Hello");
        HttpEntity<Object> request= new HttpEntity<Object>(file,header);
        ResponseEntity<Map> response = rest.exchange(this.BASE_URL_KUBOTA + this.BASE_URL + this.SINGLE_FILE, HttpMethod.POST , request , Map.class) ;
        return new ResponseEntity<Map<String,Object>>(response.getBody(), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/test", produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<Map<String,Object>> hangleSingleUpload(@RequestParam("file") MultipartFile file) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {

            System.out.println("file ========>" + file.getOriginalFilename());
//            body.add("file", new ByteArrayResource(file.getBytes()));

            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));


        } catch (IOException e) {
            e.printStackTrace();
        }



        HttpEntity requestEntity = new HttpEntity(body, headers);
        ResponseEntity<Map> response = rest.exchange(this.BASE_URL_KUBOTA + this.BASE_URL + this.SINGLE_FILE, HttpMethod.POST, requestEntity, Map.class);
        return new ResponseEntity<Map<String,Object>>(response.getBody(), HttpStatus.OK);
    }

}

 class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }
    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // we do not want to generally read the whole stream into memory ...
    }
}
