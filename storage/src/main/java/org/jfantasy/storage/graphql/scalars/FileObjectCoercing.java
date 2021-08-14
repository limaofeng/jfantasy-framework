package org.jfantasy.storage.graphql.scalars;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.storage.FileObject;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2020/3/7 7:44 下午
 */
@Slf4j
public class FileObjectCoercing implements Coercing<FileObject, Object> {

    private String fileServerUrl;

    public FileObjectCoercing(String fileServerUrl) {
        this.fileServerUrl = fileServerUrl;
    }

    @Override
    public Object serialize(Object input) throws CoercingSerializeException {
        return input instanceof FileObject ? input : input;
    }

    @Override
    public FileObject parseValue(Object input) throws CoercingParseValueException {
        String fileId = null;
        if (input instanceof String) {
            fileId = input.toString();
        }

        if (input instanceof StringValue) {
            fileId = ((StringValue) input).getValue();
        }

        if (fileId == null) {
            return null;
        }
        try {
            HttpResponse<FileObject> response = Unirest.get(this.fileServerUrl + "/files/" + fileId).asObject(FileObject.class);
            return response.getBody();
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public FileObject parseLiteral(Object input) throws CoercingParseLiteralException {
        return parseValue(input);
    }
}
