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

angular.module('indexControllers', []).controller('index',
		[ '$scope', '$http', function($scope, $http) {
			$http({
				method : 'GET',
				url : 'get'
			}).then(function(response) {
				$scope.data = response.data;
			}, function(response) {
			});
		} ])
