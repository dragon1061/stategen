/*
 * Copyright (C) 2018 niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.spring.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.stategen.framework.response.FastJsonResponseUtil;
import org.stategen.framework.util.OptionalUtil;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.util.IOUtils;

import lombok.Cleanup;

/**
 * The Class FastJsonHttpMessageConverter.
 */
public class FastJsonHttpMessageConverter extends com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter
        implements InitializingBean {
    
    /**
     * 用和不用stringconvertor有以下几个问题，因此改造fastjson解决 1. 当返回值为String,但需要包装反回时，stringconvertor会有返回值类型检测错误 2. 当返回值为String,再不需要包装时，比如
     * test,fastjson会强行包装为 "test",因些需要plaintString开关
     */
    private Boolean plainString = false;
    
    public Boolean getPlainString() {
        return plainString;
    }
    
    public void setPlainString(Boolean plaintString) {
        this.plainString = plaintString;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        FastJsonResponseUtil.FASTJSON_HTTP_MESSAGE_CONVERTOR = this;
    }
    
    @Override
    protected boolean canRead(MediaType mediaType) {
        boolean result = super.canRead(mediaType);
        if (!result) {
            //spring 在没有 获取 mediaType 强行用MediaType.APPLICATION_OCTET_STREAM_VALUE 检测
            result = MediaType.APPLICATION_OCTET_STREAM == mediaType;
        }
        return result;
    }
    
    @Override
    protected void writeInternal(
            Object obj,
            HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        
        //fastjson设置为plainString时，直接写入String 张三 =>张三 而不是 "张三"
        if (plainString && obj != null && obj instanceof String) {
            //string 直接写入string,不加双引号
            FastJsonConfig fastJsonConfig = getFastJsonConfig();
            
            String                text   = (String) obj;
            @Cleanup
            ByteArrayOutputStream outnew = new ByteArrayOutputStream();
            
            outnew.write(text.getBytes(fastJsonConfig.getCharset()));
            
            //headers先获得
            HttpHeaders  headers = outputMessage.getHeaders();
            
            //1.2.70中不设置，让它自己计算，更准确,否则不能在swagger中显示
            //if (fastJsonConfig.isWriteContentLength()) {
                //int len = text.length();
                //headers.setContentLength(len);
            //}
            headers.setContentType(MediaType.TEXT_PLAIN);
            
            //chrome中的response可以看到,swagger2中不展示，应该不是bug 
            OutputStream body = outputMessage.getBody();
            outnew.writeTo(body);
            //显示调用关闭 ，不然在 swagger中，显示 {"error": "no response from server" }
            body.close();
            if (logger.isDebugEnabled()) {
                //不能打印text,否则有泄密的风险
                logger.debug(new StringBuilder("==>fastjson write a plain text, len:").append(text.length()).toString());
            }
            
            //clean
            text = null;
            return;
        }
        
        super.writeInternal(obj, outputMessage);
        
    }
    
    private ByteBuffer getByteBuffer(InputStream is) throws IOException {
        byte[] bytes  = new byte[1024 * 64];
        int    offset = 0;
        for (;;) {
            int readCount = is.read(bytes, offset, bytes.length - offset);
            if (readCount == -1) {
                break;
            }
            offset += readCount;
            if (offset == bytes.length) {
                byte[] newBytes = new byte[bytes.length * 3 / 2];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                bytes = newBytes;
            }
        }
        return ByteBuffer.wrap(bytes, 0, offset);
    }
    
    @Override
    public Object read(
            Type type,
            Class<?> contextClass,
            HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        type = getType(type, contextClass);
        
        //fastjson直接读取String
        if (type == String.class) {
            InputStream    is             = inputMessage.getBody();
            FastJsonConfig fastJsonConfig = getFastJsonConfig();
            Charset        charset        = fastJsonConfig.getCharset();
            charset = OptionalUtil.ifNull(charset, IOUtils.UTF8);
            ByteBuffer byteBuffer = getByteBuffer(is);
            
            String result = new String(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit(), charset);
            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder("==>fastjson read plain text:\n").append(result).toString());
            }
            return result;
        }
        return super.read(type, contextClass, inputMessage);
    }
    
}
