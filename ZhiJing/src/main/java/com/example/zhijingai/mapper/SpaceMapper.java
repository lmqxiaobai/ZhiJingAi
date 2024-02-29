package com.example.zhijingai.mapper;

import com.example.zhijingai.entitys.dto.SpacePageQueryDTO;
import com.example.zhijingai.entitys.entity.Space;
import com.example.zhijingai.entitys.vo.SpaceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SpaceMapper {
    /**
     * 添加文件
     * @param space
     */
    @Insert("insert into space(name,user_id, status, file, create_time, create_user, update_time, update_user, photo)" +
            " VALUES (#{name},#{userId}, #{status}, #{file}, #{createTime}, #{createUser}, #{updateTime}, #{updateUser}, #{photo})")
    void insert(Space space);

    /**
     * 批量删除文件
     * @param ids
     */
    void deleteList(List<Long> ids);

    /**
     * 根据字段查询
     * @param space
     * @return
     */
    List<Space> select(Space space);

    /**
     * 修改text字段信息
     * @param space
     */
    void update(Space space);

    /**
     * 分页查询
     * @param spacePageQueryDTO
     * @return
     */
    Page<SpaceVO> pageQuery(SpacePageQueryDTO spacePageQueryDTO);

    /**
     * 查询回收站中超过30天的文件
     * @param status
     * @param time
     * @return
     */
    @Select("select * from space where status = #{status} and update_time < #{time}")
    List<Space> getByUpdateTimeAndStatus(int status, LocalDateTime time);

    /**
     * 彻底删除文件
     * @param id
     */
    @Delete("delete from space where id = #{id}")
    void delete(Long id);
}
