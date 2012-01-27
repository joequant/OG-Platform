/**
 * @copyright 2009 - present by OpenGamma Inc
 * @license See distribution for license
 * @colordef #fffee5; light yellow for edit mode (on hover)
 */
$.register_module({
    name: 'og.common.util.ui.content_editable',
    dependencies: ['og.common.util.ui', 'og.common.routes'],
    obj: function () {
        var ui = og.common.util.ui;
        return function (config) {
            var attr = 'data-og-editable', $attr = $('[' + attr + ']'), rest_options = {},
                handler = (config || (config = {})).handler || $.noop, pre_dispatch = config.pre_dispatch,
                css_edit = {'background-color': '#fffee5'}, css_not_edit = {'background-color': 'transparent'};
            if (typeof handler !== 'function') throw new TypeError(': config.handler must be a function');
            $attr.hover(function () {$(this).css(css_edit);}, function () {$(this).css(css_not_edit);});
            $attr.unbind('click').bind('click', function (e) {
                e.preventDefault();
                var target = e.target, value = $(target).html(), type = $(target).attr(attr);
                // get any additional information from data attributes on the element (like node, id, quantity)
                $.outer(target).split(' ').map(function (val) {
                    if (/^data-og/.test(val))
                        rest_options[val.replace(/^data-og-(.+?)=.+$/, '$1')] = val.replace(/^.+="(.+?)".*/, '$1');
                });
                delete rest_options.editable;
                ui.dialog({
                    type: 'input',
                    title: 'Edit ' + type + ': "' + value + '"',
                    fields: [{type: 'input', name: 'New Value', id: type, value: value}],
                    buttons: {
                        'OK': function () {
                            ui.dialog({action: 'close', type: 'input'});
                            rest_options[type] = ui.dialog({return_field_value: type});
                            rest_options.handler = function (result) {
                                if (result.error) return ui.dialog({type: 'error', message: result.message});
                                handler(result);
                            };
                            if (pre_dispatch) return pre_dispatch(rest_options, function (rest_options) {
                                og.api.rest[og.common.routes.current().page.substring(1)].put(rest_options);
                            });
                            og.api.rest[og.common.routes.current().page.substring(1)].put(rest_options);
                        },
                        'Cancel': function () {$(this).dialog('close');}
                    }
                });
            });
        };

    }
});