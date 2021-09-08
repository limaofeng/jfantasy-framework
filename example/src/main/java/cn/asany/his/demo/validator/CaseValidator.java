package cn.asany.his.demo.validator;

import org.jfantasy.framework.spring.validation.ValidationException;
import org.jfantasy.framework.spring.validation.Validator;
import org.jfantasy.framework.util.common.StringUtil;

public class CaseValidator implements Validator<String> {
  @Override
  public void validate(String value) throws ValidationException {
    if (StringUtil.isChinese(value)) {
      throw new ValidationException("不能有中文");
    }
  }
}
