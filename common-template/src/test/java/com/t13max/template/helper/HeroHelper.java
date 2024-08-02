package com.t13max.template.helper;

import com.t13max.template.ITemplate;
import com.t13max.template.entity.HeroTemplate;
import com.t13max.template.helper.TemplateHelper;

/**
 * @author: t13max
 * @since: 15:14 2024/5/23
 */
public class HeroHelper extends TemplateHelper<HeroTemplate> {

    public HeroHelper() {
        super("hero.xlsx");
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
