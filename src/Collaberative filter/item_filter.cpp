#include <iostream>
#include <cstdlib>
#include <string>
#include <fstream>
#include <vector>
#include <cmath>
#include <algorithm>
#include <sstream>
#include <iterator>
#include <unordered_map>
#include <ctime>

using namespace std;

/*	user informatoin */
struct user_info {
	string username;
	vector<string> group;
	vector<string> recommend;

	user_info(string &u) {
		username = u;
	}
};

/*	main function */
int main() {
	ifstream ifile("/Users/Elvis/Dropbox/big data/data/total data/user_group.txt"); 
	//ifstream ifile("/Users/Elvis/Dropbox/big data/data/test data/user_group.txt"); 
	ofstream ofile("item_filter_output.txt");
	string username, group, curr_uname;
	vector<user_info> data;
	unordered_map<string, int> map; 
	unordered_map<string, int> map_relative;
	clock_t start = clock();

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

		if(map.find(group) != map.end())
			map[group]++;
		else 
			map[group] = 1;
	}

/*	calculate weight between items */
	for(int i = 0; i < data.size(); i++) {
		for(int j = 0; j < data[i].group.size(); j++) {
			for(int k = j+1; k < data[i].group.size(); k++) {
				string tmp;
				if(data[i].group[j] > data[i].group[k])
					tmp = data[i].group[j] + "." + data[i].group[k];
				else 
					tmp = data[i].group[k] + "." + data[i].group[j];

				if(map_relative.find(tmp) == map_relative.end()) {
					map_relative[tmp] = 1;
				}
			}
		}
	}

	user_info first_entry(data[3].username);
	first_entry.group = data[3].group;

	for(unordered_map<string, int>::iterator it = map.begin(); it != map.end(); it++) {
		for(int i = 0; i < first_entry.group.size(); i++) {
			string tmp;
			if(it->first > first_entry.group[i]) {
				tmp = it->first + "." + first_entry.group[i];
				if(map_relative.find(tmp) != map_relative.end()) {
					first_entry.recommend.push_back(it->first);
				}
			}
		}
	}

/*	calculate predicts */
	for(int i = 0; i < first_entry.recommend.size(); i++) {
		ofile << first_entry.recommend[i] << endl;
	}

	cout << clock() - start << endl;

	ifile.close();
	ofile.close();

	return 0;
}