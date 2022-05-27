package cn.asany.example.demo.converter;

import cn.asany.example.demo.domain.User;
import cn.asany.example.demo.graphql.inputs.UserCreateInput;
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
