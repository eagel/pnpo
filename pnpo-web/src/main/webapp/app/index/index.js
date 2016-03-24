"use restrict";

angular.module('index', [ 'ngRoute', 'indexControllers' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/index', {
				templateUrl : 'app/index/index.html',
				controller : 'index'
			}).otherwise({
				redirectTo : '/index'
			});
		} ]);

angular.module('indexControllers', []).controller(
		'index',
		[
				'$scope',
				'$http',
				"$sce",
				function($scope, $http, $sce) {
					$http({
						method : 'GET',
						url : 'requests/index/get'
					}).then(
							function(response) {
								$scope.data = response.data;
								for (i in $scope.data) {
									$scope.data[i].data = $sce
											.trustAsHtml($scope.data[i].data);
								}
							}, function(response) {
							});
				} ])
