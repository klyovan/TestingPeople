package com.netcracker.testerritto.dao;

import com.netcracker.testerritto.mappers.RemarkRowMapper;
import com.netcracker.testerritto.models.Remark;
import com.netcracker.testerritto.properties.AttrtypeProperties;
import com.netcracker.testerritto.properties.ObjtypeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Repository
@Transactional
public class RemarkDAO {

    private String GET_REMARK_BY_ID =
        "select    \n" +
        "    remarks.object_id as id,    \n" +
        "    remark_text.value as text,    \n" +
        "    sender.reference as user_id,    \n" +
        "    caused_by_question.reference  as question_id,    \n" +
        "    recipient.reference as recipient_id,\n" +
        "    viewed_status.value as viewed\n" +
        "from    \n" +
        "    objects remarks,    \n" +
        "    attributes remark_text,  \n" +
        "    attributes viewed_status,\n" +
        "    objreference sender,    \n" +
        "    objreference caused_by_question,    \n" +
        "    objreference recipient   \n" +
        "where    \n" +
        "    remarks.object_id = ? /* remarkId */    \n" +
        "    and remarks.object_id = remark_text.object_id    \n" +
        "    and remark_text.attr_id = 8 /* REMARK_TEXT */    \n" +
        "    and remarks.object_id = viewed_status.object_id\n" +
        "    and viewed_status.attr_id = 39 /* REMARK_VIEWED */\n" +
        "    and remarks.object_id = sender.object_id    \n" +
        "    and sender.attr_id = 26 /* SEND */    \n" +
        "    and remarks.object_id = caused_by_question.object_id    \n" +
        "    and caused_by_question.attr_id = 28 /* CAUSED_BY */    \n" +
        "    and recipient.attr_id = 27  /*PROCESS_BY*/    \n" +
        "    and recipient.object_id = remarks.object_id ";

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    public RemarkDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Remark getRemarkById(BigInteger remarkId) {
        return jdbcTemplate.queryForObject(GET_REMARK_BY_ID, new Object[]{remarkId.toString()}, new RemarkRowMapper());
    }

    public BigInteger createRemark(Remark remark) {
        return new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectTypeId(ObjtypeProperties.REMARK)
            .setName("Remark")
            .setStringAttribute(AttrtypeProperties.TEXT, remark.getText())
            .setStringAttribute(AttrtypeProperties.REMARK_VIEWED, "0")
            .setReference(AttrtypeProperties.SEND, remark.getUserSenderId())
            .setReference(AttrtypeProperties.CAUSED_BY, remark.getQuestionId())
            .setReference(AttrtypeProperties.PROCESS_BY, remark.getUserRecipientId())
            .create();
}

    public void deleteRemark(BigInteger remarkId) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(remarkId)
            .delete();
    }

    public void updateRemarkViewStatus(BigInteger remarkId) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(remarkId)
            .setStringAttribute(AttrtypeProperties.REMARK_VIEWED, "1")
            .update();
    }
}
