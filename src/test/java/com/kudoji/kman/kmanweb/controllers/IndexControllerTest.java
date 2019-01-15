/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class IndexControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIndex() throws Exception{
        mockMvc.perform(get("/")).
                andExpect(status().isOk()).
                andExpect(view().name("index")).
                andExpect(content().string(containsString("kman-web")));
    }
}
