<#import "parts/common.ftlh" as c>
<@c.page>
User editor
<form action="/user" method="post">
    <input type="text" name="username" value="${user.username}">
    <#list roles as role> <#--итерация по ролей-->
        <div>
<#--            выводим пометку, что данное поле активно или нет-->
            <label><input type="checkbox" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}>${role} </label>
<#--            ${user.roles?seq_contains(role)?string("checked", "")}>${role} - freemarker проверяет наличии элемента в коллекции, и возвращает значение boolean, мы это переобразуем в строку (если true - то значение выбрано, eсли false - то ничего)-->
        </div>

    </#list>
    <input type="hidden" value="${user.id}" name="userId"> <#-- идентификатор юзера-->
    <input type="hidden" value="${_csrf.token}" name="_csrf"> <#--пост форма не будет принята сервером без этого-->
    <button type="submit">Save</button><#-- кнопка сохранить-->

</form>
</@c.page>