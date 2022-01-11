package fhw.gulimall.member.dao;

import fhw.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:28:11
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
