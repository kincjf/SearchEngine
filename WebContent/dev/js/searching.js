// query search 결과에 대한 UI

var addSearchResultList = function(searchTime, searchResultMax) {
	var listContainer = $("<ul></ui>").attr({
		"data-role" : "listview",
		"data-inset" : "true",
		"data-divider-theme" : "e"
	});

	// title data
	var searchTimeView = $("<li></li>").attr({
		"data-role" : "list-divider",
		"role" : "heading"
	}).html("Total SearchTime   -   " + 0.001 * searchTime + "  second");

	// url data
	var searchResultMaxView = $("<li></li>").attr({
		"data-icon" : "refresh"
	});
	var searchResultMaxInnerView = $("<a></a>").attr({
		href : ""
	}).html("Retrieved Document   -   " + searchResultMax);
	searchResultMaxView.append(searchResultMaxInnerView);
	
	listContainer.append(searchTimeView, searchResultMaxView);

	$("#search_result_container").append(listContainer);
};

var addQueryList = function(query) {
	var queryView = $("<a></a>").attr({
		href : "",
		"data-role" : "button",
		"data-theme" : "d",
		"data-icon" : "info"
	}).html("Query  -  " + query).appendTo($("#search_result_container"));
};

var addSearchDataList = function(searchResult) {
	// whole search data container
	var listContainer = $("<ul></ui>").attr({
		"data-role" : "listview",
		"data-inset" : "true",
		"data-divider-theme" : "f"
	});

	// title data
	var titleView = $("<li></li>").attr({
		'data-role' : "list-divider",
		'role' : "heading"
	}).html(searchResult.title);

	// url data
	var urlView = $("<li></li>").attr({
		'data-icon' : "refresh"
	});
	var urlInnerView = $("<a></a>").attr({
		href : searchResult.url,
		target : "_blank"
	}).html(searchResult.url);
	urlView.append(urlInnerView);

	// contributor data
	var contributorView = $("<li></li>").attr({
		'data-icon' : 'refresh'
	});
	var contributorInnerView = $("<a></a>").attr({
		href : ""
	}).html("Contributor  -  " + searchResult.contributors);
	contributorView.append(contributorInnerView);

	// hits data
	var hitsView = $("<li></li>").attr({
		'data-icon' : "refresh"
	});
	var hitsInnerView = $("<a></a>").attr({
		href : ""
	}).html("Hits  -  " + searchResult.hits);
	hitsView.append(hitsInnerView);

	listContainer.append(titleView, urlView, contributorView, hitsView);

	$("#search_result_container").append(listContainer);
};

// query에 대햐여 ajax로 요청함
var getSearchList = function() {
	// var query = $("#query").val();
	// ajax의 변수 scope가 궁금하다 뭐지...?

	if (query == "" | null) {
		console.log("no queries");
		return false;
	}

	var params = $.param({
		QUERY : $("#query").val(),
		PAGE : 1
	});

	$.ajax({
		url : "/SearchEngine/searchAction.sf",
		type : "get",
		data : params,
		dataType : "json",
		contentType: 'application/json',
        mimeType: 'application/json',
		success : function(data) {
			clearList($("#search_result_container"));

			addQueryList(data.QUERY);
			$(data.SEARCH_RESULT).each(function(index, Element) {
				addSearchDataList(this);
			});
			addSearchResultList(data.SEARCH_TIME, data.SEARCH_RESULT_MAX);
			
			$('#search_result_container').trigger('create');
		},
		error : function(xhr, textStatus, errorThrown) {
			console.log("getSearchList error - " + textStatus + " " + xhr.status
					+ " " + errorThrown);
		}
	});
};