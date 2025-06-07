package com.swinger.impl;

import com.swinger.api.MemberAccessor;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReflectionMemberAccessorTest {
    private MemberAccessor memberAccessor = new ReflectionMemberAccessor();

    @Test
    public void testPropertyAccess() throws Exception {
        // given
        JLabel label = new JLabel();
        JTextField textField = new JTextField();
        LayoutManager lm1 = new FlowLayout();
        LayoutManager lm2 = new CardLayout();

        // when
        memberAccessor.setProperty(label, "text", "text1");
        memberAccessor.setProperty(label, "layout", lm1);
        memberAccessor.setProperty(textField, "text", "text2");
        memberAccessor.setProperty(textField, "layout", lm2);

        // then
        assertThat(memberAccessor.getProperty(label, "text")).isEqualTo("text1");
        assertThat(memberAccessor.getProperty(label, "layout")).isEqualTo(lm1);
        assertThat(memberAccessor.getProperty(textField, "text")).isEqualTo("text2");
        assertThat(memberAccessor.getProperty(textField, "layout")).isEqualTo(lm2);
    }

    @Test
    public void testExceptions() {
        // given
        JLabel label = new JLabel();

        // then
        assertThatThrownBy(() -> memberAccessor.setProperty(label, "invalid", "text1"))
                .hasMessage("No setter for invalid in javax.swing.JLabel");
        assertThatThrownBy(() -> memberAccessor.getProperty(label, "invalid"))
                .hasMessage("No getter for invalid in javax.swing.JLabel");
    }

}