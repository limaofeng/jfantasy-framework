package cn.asany.his.demo.converter;

import cn.asany.his.demo.bean.User;
import cn.asany.his.demo.graphql.inputs.UserCreateInput;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConverter {

  User toUser(UserCreateInput input);
}
