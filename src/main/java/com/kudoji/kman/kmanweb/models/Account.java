/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Account {
    private final int id;
    private final String name;
}
