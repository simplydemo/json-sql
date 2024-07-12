package io.github.simplydemo;

import io.github.thenovaworks.json.query.JsonQueryHandler;
import io.github.thenovaworks.json.query.ResultMapUtils;
import io.github.thenovaworks.json.query.SqlSession;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonQueryHandlerTest {

    private static final String toJsonString(String filepath) {
        try {
            InputStream inputStream = JsonQueryHandlerTest.class.getResourceAsStream(filepath);
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                return bufferedReader.lines().collect(Collectors.joining("\n"));
            } else {
                return "";
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return "";
        }
    }


    @Test
    public void testHealthResultMapUtils() {
        String json = toJsonString("/aws-health-event.json");

        // @formatter:off
        String sql = """
select  id, detail-type, source, account, time, region, resources, 
        detail.eventArn, 
        detail.service, 
        detail.eventTypeCode, 
        detail.eventTypeCategory, 
        detail.eventScopeCode, 
        detail.startTime, 
        detail.lastUpdatedTime, 
        detail.statusCode, 
        detail.eventRegion, 
        detail.eventDescription, 
        detail.affectedEntities,
        detail.affectedAccount
from    health
""";
        // @formatter:on

        SqlSession sqlSession = new SqlSession(new JsonQueryHandler("health", json));
        Map<String, Object> rs = sqlSession.queryForObject(sql, Map.of());
        List<Map<String, Object>> list = ResultMapUtils.toList((List<Map<String, Object>>) rs.get("detail.eventDescription"));
        Map<String, Object> data = list.stream().findFirst().orElse(null);
        assertEquals("en_US", data.get("language"));
        System.out.println(data);
    }


    @Test
    public void testUsersWhereParams() {
        String json = toJsonString("/users.json");
        // @formatter:off
        String sql = """
select  index, guid, isActive, balance, age,
        eyeColor, name, gender, company, email,
        phone, address, registered, latitude, longitude,
        tags, friends, greeting, favoriteFruit
from    member
where   gender = :gender
and     age <= :age
and     eyeColor = :eyeColor
""";
        // @formatter:on
        SqlSession sqlSession = new SqlSession(new JsonQueryHandler("member", json));
        List<Map<?, Object>> records = sqlSession.queryForList(sql, Map.of("gender", "female", "age", 30, "eyeColor", "blue"));
        records.forEach(System.out::println);
        // System.out.println("keys: " + sqlSession.getKeys(2));
        assertEquals(2, records.size());
    }


}
