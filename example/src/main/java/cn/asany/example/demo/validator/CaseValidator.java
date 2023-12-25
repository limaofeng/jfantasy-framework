package cn.asany.example.demo.validator;

import net.asany.jfantasy.framework.spring.validation.ValidationException;
import net.asany.jfantasy.framework.spring.validation.Validator;
import net.asany.jfantasy.framework.util.common.StringUtil;

public class CaseValidator implements Validator<String> {
  @Override
  public void validate(String value) throws ValidationException {
    if (StringUtil.isChinese(value)) {
      throw new ValidationException("不能有中文");
    }
  }
}
