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
	double weight;
	double average_rate;
	double sum_of_square;

	user_info(string &u) {
		username = u;
		weight = 0;
	}
};

struct group_pred {
	string groupname;
	double predict;
};

/*	comparator for sort */
static bool cmp(const user_info &a, const user_info &b) {
	return a.weight > b.weight;
}

static bool cmp2(const group_pred &a, const group_pred &b) {
	return a.predict > b.predict;
}

/*	main function */
int main() {
	ifstream ifile("/Users/Elvis/Dropbox/big data/data/total data/user_group.txt"); 
	//ifstream ifile("/Users/Elvis/Dropbox/big data/data/test data/user_group.txt"); 
	ofstream ofile("neighborhood_filter_output.txt");
	string username, group, curr_uname;
	vector<user_info> data;
	vector<group_pred> prediction;
	unordered_map<string, double> map; 
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

/*	calculate cosin weight between users */
	for (int i = 0; i < data.size(); i++) {
		data[i].average_rate = (double) data[i].group.size() / map.size();

		double sum = 0;
		for(int j = 0; j < data[i].group.size(); j++) {
			sum += pow((1 - data[i].average_rate), 2);
		}

		data[i].sum_of_square = sum;
	}

	/*	use first entry as active user */
	user_info first_entry(data[3].username);
	first_entry.group = data[3].group;
	first_entry.sum_of_square = data[3].sum_of_square;
	first_entry.average_rate = data[3].average_rate;

	for (int i = 0; i < data.size(); i++) {
		double sum = 0;
		for (int j = 0; j < first_entry.group.size(); j++) {
			for (int k = 0; k < data[i].group.size(); k++) {
				if (data[i].group[k] == first_entry.group[j])
					sum += double (1 - first_entry.average_rate)*(1 - data[i].average_rate);
			}
		}

		data[i].weight = sum / sqrt(data[i].sum_of_square*first_entry.sum_of_square);
	}

/*	select k users that have the highest weight */
	sort(data.begin(), data.end(), cmp);

/* 	export recommend users */
	for(int i = 0; i < 10; i++) {
		ofile << data[i].username;
		ofile << "	" << data[i].weight << endl; 
	}

	ifile.close();
	ofile.close();

/*	caculate predict weight for all items */
	for (unordered_map<string, double>::iterator it = map.begin(); it != map.end(); it++) {
		string item = it->first;
		double sum_w = 0, sum = 0;

		for(int i = 0; i < data.size(); i++) {
			for(int j = 0; j < data[i].group.size(); j++) {
				if (data[i].group[j] == item) {
					sum_w += data[i].weight;
					sum += (1 - data[i].average_rate) * data[i].weight;
					//cout << data[i].group[j] << endl;
				}
			}
		}

		group_pred tmp;
		tmp.groupname = item;
		if (sum_w == 0) 
			tmp.predict = 0;
		else
			tmp.predict = sum / sum_w;

		prediction.push_back(tmp);
	}

/*	get the first K items */
	sort(prediction.begin(), prediction.end(), cmp2);
	ofile.open("neighborhood_filter_predict.txt");
	for(int i = 0; i < 10; i++) {
		ofile << prediction[i].groupname << "	" << prediction[i].predict << endl;
	}

	cout << clock() - start << endl;

	return 0;
}