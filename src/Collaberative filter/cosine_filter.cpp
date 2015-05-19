#include <iostream>
#include <cstdlib>
#include <string>
#include <fstream>
#include <vector>
#include <math.h>
#include <algorithm>
#include <sstream>
#include <iterator>
#include <unordered_map>

using namespace std;

/*	user informatoin */
struct user_info {
	string username;
	vector<string> group;
	double weight;

	user_info(string &u) {
		username = u;
		weight = 0;
	}
};

/*	comparator for sort */
static bool cmp(const user_info &a, const user_info &b) {
	return a.weight > b.weight;
}

/*	main function */
int main() {
	ifstream ifile("/Users/Elvis/Dropbox/big data/data/total data/user_group.txt"); 
	//ifstream ifile("/Users/Elvis/Dropbox/big data/data/test data/user_group.txt"); 
	ofstream ofile("cosine_filter_output.csv");
	string username, group, curr_uname;
	vector<user_info> data;

/*load user data*/
	ifile >> username >> group;
	curr_uname = username;
	user_info tmp(username);
	tmp.group.push_back(group);
	data.push_back(tmp);

	while(ifile.good()) {
		ifile >> username >> group;
		if (username == curr_uname) {
			data.back().group.push_back(group);
		} else {
			user_info entry(username);
			entry.group.push_back(group);
			data.push_back(entry);
			curr_uname = username;
		}
	}

/*	calculate cosin weight between users */
	/*	use first entry as active user */
	user_info first_entry(data[0].username);
	first_entry.group = data[0].group;

	for(int i = 0; i < data.size(); i++) {
		int total = 0;
		for(int j = 0; j < first_entry.group.size(); j++) {
			for(int k = 0; k < data[i].group.size(); k++) {
				if (first_entry.group[j] == data[i].group[k])
					total++;
			}
		}
		data[i].weight = (double)total / (double)(sqrt(first_entry.group.size())\
				*sqrt(data[i].group.size())); 
	}

/*	select k users that have the highest weight */
	sort(data.begin(), data.end(), cmp);

/* 	export recommend users */
	for(int i = 0; i < 10; i++) {
/*		for(int j = 0; j < data[i].group.size(); j++) {
			ofile << data[i].username << "," << data[i].group[j] << endl;
		}*/
		ofile << data[i].weight << endl;
	}

	ifile.close();
	ofile.close();

	return 0;
}