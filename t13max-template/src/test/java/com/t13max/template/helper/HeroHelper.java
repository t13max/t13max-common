package com.t13max.template.helper;

import com.t13max.template.HeroTemplate;
import com.t13max.template.ITemplate;

/**
 * @author: t13max
 * @since: 15:14 2024/5/23
 */
public class HeroHelper extends TemplateHelper<HeroTemplate> {

    public HeroHelper() {
        super("excel/hero.xlsx");
    }

    @Override
    public boolean configCheck() {
        return true;
    }

    @Override
    public Class<? extends ITemplate> getClazz() {
        return HeroTemplate.class;
    }

}
