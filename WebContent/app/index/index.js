"use restrict";

angular.module('index', [ 'ngRoute', 'indexControllers' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/get', {
				templateUrl : 'get',
				controller : 'get'
			}).otherwise({
				redirectTo : '/get'
			});
		} ]);

angular.module('indexControllers', []).controller('get',
		[ '$scope', function($scope) {
			
		} ])
