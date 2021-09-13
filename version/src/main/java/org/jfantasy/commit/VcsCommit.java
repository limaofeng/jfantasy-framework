package org.jfantasy.commit;

import org.apache.commons.lang3.StringUtils;
import org.jfantasy.autoconfigure.VersionRabbitAutoConfiguration;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Description 历史数据提交
 * @author ChenWenJie
 * @Data 2020/11/10 10:56 上午
 **/
public class VcsCommit {
    private final static String defaultLibraryType = "Root";

    /**
     * 添加历史数据
     * @param versionLibrary 历史库
     * @param dataId 数据id
     * @param jsonData 历史数据json串
     */
    public static void addVersion(String versionLibrary,Object dataId,String jsonData){
         commit(versionLibrary,defaultLibraryType, dataId, jsonData,false);
    }

    /**
     * 删除历史数据
     * @param versionLibrary 历史库
     * @param dataId 数据id
     * @param jsonData 历史数据json串
     */
    public static void removeVersion(String versionLibrary,Object dataId,String jsonData){
        commit(versionLibrary,defaultLibraryType, dataId, jsonData,true);
    }

    /**
     * 提交数据
     * @param versionLibrary 版本库
     * @param libraryType 库类型
     * @param dataId 数据id
     * @param jsonData 历史数据json串
     * @param deleted  添加false  删除true
     */
    public static void commit(String versionLibrary,String libraryType,Object dataId,String jsonData,Boolean deleted){
        RabbitTemplate rabbitTempate = SpringContextUtil.getBeanByType(RabbitTemplate.class);
        CommitRequest commitRequest = CommitRequest.builder()
                .code(versionCode(versionLibrary, libraryType))
                .employeeId(SpringSecurityUtils.getCurrentUser().getUid())
                .id(String.valueOf(dataId))
                .deleted(deleted)
                .jsonData(jsonData).build();
        rabbitTempate.convertAndSend(VersionRabbitAutoConfiguration.DIRECT_EXCHANGE_NAME,
                VersionRabbitAutoConfiguration.BINGDING_KEY_VCS, JSON.serialize(commitRequest));
    }



    /**
     * 拼装版本库编码
     * @param libraryCode 库代码、库类型
     * @return
     */
    private static String versionCode(String... libraryCode){
        return StringUtils.join(libraryCode, ".");
    }
}
