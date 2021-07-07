package org.jfantasy.framework.util.ognl;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OgnlBean {

    private String name;

    private Long number;

    private String[] names;

    private List<String> listNames;

    private List<OgnlBean> list;

    private OgnlBean bean;

    private OgnlBean[] array;


}
