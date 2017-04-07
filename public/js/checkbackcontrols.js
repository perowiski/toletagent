/**
 * Created by tolet on 1/22/16.
 */
$('.checkStatus').change(function() {
    //alert("Check status has changed");

    var exitedControl = $(this).attr("data-exitP");
    var issuesControl = $(this).attr("data-issueP");

    //hide all visible controls
    $('.checkStatus ~ p').hide();
    //Reset all other select boxes to default
    $(".checkStatus ~ p").find("select option").prop("selected", false);

    //Reset and hide all select boxes in issues control to default
    $('.checkStatus ~ div').hide();
    $(".checkStatus ~ div").find("select option").prop("selected", false);

    var value = $(this).val();

    if (value === "NO_ISSUES") {

    }
    else if (value === "ISSUES") {
        $(issuesControl).show();
    }
    else if (value === "EXITED") {
        $(exitedControl).show();
    }
    else if (value === "DETECT_DEAL") {

    }

});


$('.exitType').click(function() {

    var closedControl = $(this).attr("data-closedType");

    //reset the form
    $(closedControl).hide();

    var value = $(this).val();
    if (value === "RETAINED") {

    }
    else if (value === "CLOSED") {
        $(closedControl).show();
    }

});




$('.issueType').click(function() {
    var execControl = $(this).attr("data-execType");
    var nonSolvableControl = $(this).attr("data-nonSolvable");

    //hide all visible controls
    $('.issueType ~ p').hide();
    //Reset all other select boxes to default
    $(".issueType ~ p").find("select option").prop("selected", false);

    var value = $(this).val();

    if (value === "NONSOLVABLE") {
        $(nonSolvableControl).show();
    }
    else if (value === "SOLVABLE") {
        $(execControl).show();
    }

});


$('.execStatus').click(function() {
    var value = $(this).val();

    if (value === "NO_ISSUES") {

    }
    else if (value === "ISSUES") {
        $(issuesControl).show();
    }
    else if (value === "EXITED") {
        $(exitedControl).show();
    }
    else if (value === "DETECT_DEAL") {

    }

});
